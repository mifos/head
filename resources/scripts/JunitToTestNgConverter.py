#!/usr/bin/env python
"""Converts Java JUnit3.8 test classes to TestNG, with ordering dependencies.
"""
import sys

# TODO
# read in list of files in this suite
# make group names for them
#
# Class annotation
#   get suiteName and previous suite name from command line
#   do before and after annotations

class JunitToTestNgConverter:
   def __init__(self):
      self.testNgIgnoreImportLine = "import org.testng.annotations.Ignore;"
      self.testNgTestImportLine = "import org.testng.annotations.Test;"
      self.testNgBeforeMethodImportLine = "import org.testng.annotations.BeforeMethod;"
      self.testNgAfterMethodImportLine = "import org.testng.annotations.AfterMethod;"
      self.classAnnotationLine = '@Test(groups={"%s", "%s", "%s"}, dependsOnGroups={%s})'
      self.ignoreAnnotationLine = "@Ignore"
      self.testMethodAnnotationLine = "@Test"
      self.testMethodAnnotationWithDependsLine = "@Test(dependsOnMethods={'%s'})"
      self.beforeMethodAnnotationLine = "@BeforeMethod"
      self.afterMethodAnnotationLine = "@AfterMethod"
      self.parseArgs()

   def parseArgs(self):
      if len(sys.argv) != 4:
         self.usage()
         sys.exit(0)         
      self.currentSuiteName = sys.argv[1]
      self.currentSuiteClassesFile = sys.argv[2]
      self.filename = sys.argv[3]
      self.suiteClasses = self.readEntireFileAsLines(self.currentSuiteClassesFile)
      
   def readEntireFile(self, filename):
      file = open(filename)
      contents = file.read()
      file.close()
      return contents

   def splitFileIntoLines(self, contents):
      return contents.split('\n')
   
   def readEntireFileAsLines(self, filename):
      return self.splitFileIntoLines(self.readEntireFile(filename))
   
   def getDependsOnClass(self, currentClassName):
      index = 0
      for className in self.suiteClasses:
         if currentClassName == className:
            dependsOnIndex = index - 1
            if dependsOnIndex < 0:
               return ""
            else:
               return '"%s"' % self.suiteClasses[dependsOnIndex]
         index += 1
      return ""
   
   def getClassAnnotationLine(self):
      self.dependsOnClass = self.getDependsOnClass(self.className)
      return self.classAnnotationLine % (self.testType, self.currentSuiteName, self.className, self.dependsOnClass)
   
   def getListOfTestMethodNames(self):
      allMethodNames = []
      testMethodNames = []
      methodLines = {}
      index = 0
      for line in self.lines:
         currentLine = line.strip()
         if (currentLine.startswith("public void test") or  
             currentLine.startswith("public void xtest") or 
             currentLine.startswith("public void setUp") or 
             currentLine.startswith("public void tearDown") ) :
            words = currentLine.split(' ')
            methodName = words[2].split('(')[0]
            allMethodNames.append(methodName)
            if methodName.startswith('t'):
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
   
   def getImportLines(self):
      importLines = []
      if self.xTestMethodsExist():
         importLines.append(self.testNgIgnoreImportLine)
      if self.setUpMethodExists():
         importLines.append(self.testNgBeforeMethodImportLine)
      if self.tearDownMethodExists():
         importLines.append(self.testNgAfterMethodImportLine)
      importLines.append(self.testNgTestImportLine)
      importLines.append('')
      importLines.append(self.getClassAnnotationLine())
      return importLines
   
   def insertImportAndAnnotationLine(self):
      self.allMethodNames, self.testMethodNames, self.methodLines = self.getListOfTestMethodNames()
      for index in xrange(0, len(self.lines)):
         currentLine = self.lines[index].strip()
         if currentLine.startswith("import"):
            next
         if len(currentLine) == 0:
            next
         if currentLine.startswith("public class"):
            self.setClassInfo(currentLine)
            newlines = self.lines[:index] + self.getImportLines() + self.lines[index:]
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
      print "hello!"

   def convert(self):
      self.fileContents = self.readEntireFile(self.filename)
      self.lines = self.splitFileIntoLines(self.fileContents)
      self.insertImportAndAnnotationLine()
      self.insertTestMethodAnnotionLines()
      print '\n'.join(self.lines)
   
if __name__ == '__main__':
   JunitToTestNgConverter().convert()