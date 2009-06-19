#!/usr/bin/env python
"""Converts Java JUnit3.8 test classes to TestNG, with ordering dependencies.
"""
import os.path, sys
from gnosis.xml.objectify import XML_Objectify, DOM

class JunitToTestNgConverter:
   def __init__(self):
      self.testNgIgnoreImportLine = "import org.testng.annotations.Ignore;"
      self.testNgTestImportLine = "import org.testng.annotations.Test;"
      self.testNgBeforeMethodImportLine = "import org.testng.annotations.BeforeMethod;"
      self.testNgAfterMethodImportLine = "import org.testng.annotations.AfterMethod;"
      self.classAnnotationLine = '@Test(groups={"%s", "%s", "%s"}, dependsOnGroups={%s})'
      self.ignoreAnnotationLine = "@Ignore"
      self.testMethodAnnotationLine = "@Test"
      self.testMethodAnnotationWithDependsLine = '@Test(dependsOnMethods={"%s"})'
      self.beforeMethodAnnotationLine = "@BeforeMethod"
      self.afterMethodAnnotationLine = "@AfterMethod"
      self.parseArgs()

   def readEntireFile(self, filename):
      file = open(filename)
      contents = file.read()
      file.close()
      return contents

   def splitFileIntoLines(self, contents):
      return contents.split('\n')
   
   def isNotTestNgLine(self, line):
      testLine = line.strip()
      if testLine.startswith("import org.testng"):
         return False
      if testLine.startswith("@Test"):
         return False
      if testLine.startswith("@BeforeMethod"):
         return False
      if testLine.startswith("@AfterMethod"):
         return False
      return True
   
   def removeTestNgLines(self, lines):
      newLines = []
      for line in lines:
         if self.isNotTestNgLine(line):
            newLines.append(line)
      return newLines
   
   def readEntireFileAsLines(self, filename):
      return self.splitFileIntoLines(self.readEntireFile(filename))
   
   def getClassAnnotationLine(self, previousTestSuite, currentTestSuite, previousTestClass, currentTestClass):
      dependsOnGroups = [previousTestSuite, previousTestClass]
      dependsOnGroupsFiltered = [group for group in dependsOnGroups if group is not None]
      dependsOnGroupsString = ""
      for group in dependsOnGroupsFiltered:
         dependsOnGroupsString += '"%s", ' % group
      if dependsOnGroupsString.endswith(", "):
         dependsOnGroupsString = dependsOnGroupsString[:-2]   
      return self.classAnnotationLine % (self.testType, currentTestSuite, self.className, dependsOnGroupsString)
   
   def getListOfTestMethodNames(self):
      allMethodNames = []
      testMethodNames = []
      methodLines = {}
      index = 0
      for line in self.lines:
         currentLine = line.strip()
         if (currentLine.startswith("public void test") or  
             currentLine.startswith("public void xtest") or 
             currentLine.startswith("protected void setUp") or 
             currentLine.startswith("protected void tearDown") ) :
            words = currentLine.split(' ')
            methodName = words[2].split('(')[0]
            allMethodNames.append(methodName)
            if methodName.startswith('test'):
               testMethodNames.append(methodName)
            methodLines[methodName] = index
         index += 1
      return allMethodNames, testMethodNames, methodLines
   
   def xTestMethodsExist(self):
      for methodName in self.allMethodNames:
         if methodName.startswith('x'):
            return True
      return False

   def setUpMethodExists(self):
      for methodName in self.allMethodNames:
         if methodName == "setUp":
            return True
      return False

   def tearDownMethodExists(self):
      for methodName in self.allMethodNames:
         if methodName == "tearDown":
            return True
      return False
   
   def setClassInfo(self, line):
      words = line.strip().split(' ')
      self.className = words[2]
      testExtends = words[4]
      self.testType = "integration"
      if testExtends.endswith("IntegrationTest"):
         self.testType = "integration"
      elif testExtends == "TestCase":
         self.testType = "unit"
   
   def getImportLines(self, previousTestSuite, currentTestSuite, previousTestClass, currentTestClass):
      importLines = []
      if self.xTestMethodsExist():
         importLines.append(self.testNgIgnoreImportLine)
      if self.setUpMethodExists():
         importLines.append(self.testNgBeforeMethodImportLine)
      if self.tearDownMethodExists():
         importLines.append(self.testNgAfterMethodImportLine)
      importLines.append(self.testNgTestImportLine)
      importLines.append('')
      importLines.append(self.getClassAnnotationLine(previousTestSuite, currentTestSuite, previousTestClass, currentTestClass))
      return importLines
   
   def insertImportAndAnnotationLine(self, previousTestSuite, currentTestSuite, previousTestClass, currentTestClass):
      self.allMethodNames, self.testMethodNames, self.methodLines = self.getListOfTestMethodNames()
      for index in xrange(0, len(self.lines)):
         currentLine = self.lines[index].strip()
         if currentLine.startswith("import"):
            next
         if len(currentLine) == 0:
            next
         if currentLine.startswith("public class"):
            self.setClassInfo(currentLine)
            newlines = self.lines[:index] + self.getImportLines(previousTestSuite, currentTestSuite, previousTestClass, currentTestClass) + self.lines[index:]
            self.lines = newlines
            break
      
   def getIndent(self):
      return "    "
      
   def insertTestMethodAnnotionLines(self):
      self.allMethodNames, self.testMethodNames, self.methodLines = self.getListOfTestMethodNames()
      #print self.methodLines
      keys = self.allMethodNames
      keys.reverse()
      for key in keys:
         lineNumber = self.methodLines[key]
         if key.startswith('x'):
            annotationLine = self.getIndent() + self.ignoreAnnotationLine
         elif key.startswith('test'):
            annotationLine = self.getIndent() + self.getDependentMethodAnnotationLine(key)
         elif key.startswith('setUp'):
            annotationLine = self.getIndent() + self.beforeMethodAnnotationLine
         elif key.startswith('tearDown'):
            annotationLine = self.getIndent() + self.afterMethodAnnotationLine
         self.lines = self.lines[:lineNumber] + [annotationLine] + self.lines[lineNumber:]


   def getDependentMethodAnnotationLine(self, methodName):
      dependentMethodIndex = self.findTestMethod(methodName) - 1
      if dependentMethodIndex < 0:
         return self.testMethodAnnotationLine
      return self.testMethodAnnotationWithDependsLine % self.testMethodNames[dependentMethodIndex]

   def findTestMethod(self, methodNameToFind):
      index = 0
      for methodName in self.testMethodNames:
         if methodName == methodNameToFind:
            return index
         index += 1
      return -1

   def usage(self):
      programName = os.path.basename(sys.argv[0])
      print "%s [test source tree base directory] [testng.xml suite file]" % programName

   def convert(self, previousTestSuite, currentTestSuite, previousTestClass, currentTestClass, currentTestClassFilename):
      self.fileContents = self.readEntireFile(currentTestClassFilename)
      self.lines = self.removeTestNgLines(self.splitFileIntoLines(self.fileContents))
      self.insertImportAndAnnotationLine(previousTestSuite, currentTestSuite, previousTestClass, currentTestClass)
      self.insertTestMethodAnnotionLines()
      print '\n'.join(self.lines)
   
   def parseArgs(self):
      if len(sys.argv) != 3:
         self.usage()
         sys.exit(0)         
      self.testSourceBaseDir = sys.argv[1]
      self.suiteXmlFilename = sys.argv[2]

   def getClassNameFromPath(self, path):
      if path is None: return None
      return path.split('.')[-1]
   
   def getFullPathToTestFile(self, className):
      return os.path.join(self.testSourceBaseDir, className.replace('.', os.path.sep) + '.java')

   def main(self):
      self.parseArgs()
      previousTestSuite = None
      suiteXml = XML_Objectify(self.suiteXmlFilename, parser=DOM).make_instance()
      for test in suiteXml.test:
         currentTestSuite = test.name
         previousTestClass = None
         for aClass in test.classes.__dict__['class']:
            currentTestClass = aClass.name
            self.convert(previousTestSuite, currentTestSuite, self.getClassNameFromPath(previousTestClass), currentTestClass, self.getFullPathToTestFile(currentTestClass))
            previousTestClass = currentTestClass
         previousTestSuite = currentTestSuite
   
if __name__ == '__main__':
   JunitToTestNgConverter().main()