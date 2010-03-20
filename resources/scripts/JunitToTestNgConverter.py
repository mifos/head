#!/usr/bin/env python
"""Converts Java JUnit3.8 test classes to TestNG, with ordering dependencies.
"""
import os.path, sys
from optparse import OptionParser

# Todo:
# @ContextConfiguration
class JunitToTestNgConverter:
   def __init__(self):
      self.testNgTestImportLine = "import org.testng.annotations.Test;"
      self.testNgBeforeMethodImportLine = "import org.testng.annotations.BeforeMethod;"
      self.testNgAfterMethodImportLine = "import org.testng.annotations.AfterMethod;"
      self.classAnnotationLine = '@Test(groups={"%s", "%s"})'
      self.ignoreAnnotationLine = "@Test(enabled=false)"
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

   def writeEntireFile(self, filename, contents):
      file = open(filename, "w")
      contents = file.write(contents)
      file.close()

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
   
   def isUnnecessaryJunitLine(self, line):
      testLine = line.strip()
      if testLine.startswith("import org.junit.Test"):
         return True
      if testLine.startswith("import org.junit.Ignore"):
         return True
      if testLine.startswith("@Ignore"):
         self.ignore = True
         return True
      return False   

   def removeTestNgLines(self, lines):
      newLines = []
      for line in lines:
         if self.isNotTestNgLine(line) and not self.isUnnecessaryJunitLine(line):
            newLines.append(line)
      return newLines
   
   def readEntireFileAsLines(self, filename):
      return self.splitFileIntoLines(self.readEntireFile(filename))
   
   def getClassAnnotationLine(self, currentTestClass):
      if self.ignore:
         return self.ignoreAnnotationLine
      else:
         return self.classAnnotationLine % (currentTestClass, self.testGroup)
   
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
             currentLine.startswith("protected void tearDown") or 
             currentLine.startswith("public void setUp") or 
             currentLine.startswith("public void tearDown") ) :
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
      testExtends = None
      if len(words) > 4:
         testExtends = words[4]
      self.testType = "integration"
      if testExtends is not None and testExtends.endswith("IntegrationTest"):
         self.testType = "integration"
      elif testExtends == "TestCase":
         self.testType = "unit"
   
   def getImportLines(self, currentTestClass):
      importLines = []
      if self.setUpMethodExists():
         importLines.append(self.testNgBeforeMethodImportLine)
      if self.tearDownMethodExists():
         importLines.append(self.testNgAfterMethodImportLine)
      importLines.append(self.testNgTestImportLine)
      importLines.append('')
      importLines.append(self.getClassAnnotationLine(currentTestClass))
      return importLines
   
   def insertImportAndAnnotationLine(self, currentTestClass):
      self.allMethodNames, self.testMethodNames, self.methodLines = self.getListOfTestMethodNames()
      previousLine = ""
      for index in xrange(0, len(self.lines)):
         currentLine = self.lines[index].strip()
         if currentLine.startswith("import"):
            next
         if len(currentLine) == 0:
            next
         if currentLine.startswith("public class"):
            self.setClassInfo(currentLine)
            if not previousLine.startswith("@ContextConfiguration"):
               newIndex = index
            else:
               newIndex = index - 1
            newlines = self.lines[:newIndex] + self.getImportLines(currentTestClass) + self.lines[newIndex:]
            self.lines = newlines
            break
         previousLine = currentLine
      
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
      if self.options.addDependencies is False:
         return self.testMethodAnnotationLine
      dependentMethodIndex = self.findTestMethod(methodName) - 1
      if dependentMethodIndex < 0:
         return self.testMethodAnnotationLine
      else:
         return self.testMethodAnnotationWithDependsLine % self.testMethodNames[dependentMethodIndex]

   def findTestMethod(self, methodNameToFind):
      index = 0
      for methodName in self.testMethodNames:
         if methodName == methodNameToFind:
            return index
         index += 1
      return -1

   def convert(self, currentTestClass, currentTestClassFilename):
      self.fileContents = self.readEntireFile(currentTestClassFilename)
      if not self.isTestNgTest(self.fileContents):
         self.lines = self.removeTestNgLines(self.splitFileIntoLines(self.fileContents))
         self.insertImportAndAnnotationLine(currentTestClass)
         self.insertTestMethodAnnotionLines()
         newContents = '\n'.join(self.lines)
         self.writeEntireFile(currentTestClassFilename, newContents)
         print "Wrote %s." % currentTestClassFilename
      else:
         print "Not converting %s, it is already a TestNG test." % currentTestClassFilename
      
   def isTestNgTest(self, content):
      if content.find("import org.testng") > 0:
         return True
      else:
         return False
   
   def parseArgs(self):
      usage = "usage: %prog [options] [pathToJunitTestFileSource] [TestNGGroup]"
      parser = OptionParser(usage)
      parser.add_option("-d", "--addDependencies", action="store_true", dest="addDependencies", default=False, help="add dependencies to ensure JUnit test ordering")
      self.options, args = parser.parse_args()
      if len(args) != 2:
         parser.print_help()
         sys.exit(0)       
      self.testSourceFile = args[0]
      self.testGroup = args[1]

   def getClassNameFromPath(self, path):
      if path is None: return None
      return path.split('.')[-2].split('/')[-1]
   
   def main(self):
      self.ignore = False
      self.addDependencies = False
      self.parseArgs()
      testClasses = {}
      self.convert(self.getClassNameFromPath(self.testSourceFile), self.testSourceFile)
   
if __name__ == '__main__':
   JunitToTestNgConverter().main()