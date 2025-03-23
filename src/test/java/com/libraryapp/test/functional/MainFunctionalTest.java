package com.libraryapp.test.functional;

import static com.libraryapp.test.utils.TestUtils.businessTestFile;
import static com.libraryapp.test.utils.TestUtils.currentTest;
import static com.libraryapp.test.utils.TestUtils.testReport;
import static com.libraryapp.test.utils.TestUtils.yakshaAssert;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import mainapp.MyApp;

public class MainFunctionalTest {

    @AfterAll
    public static void afterAll() {
        testReport();
    }

    @Test
    @Order(1)
    public void testBranchCreationAndValidate() throws IOException {
        try {
            // Calling the method to check if both 'feature-a' and 'feature-b-renamed' branches exist
            String output = MyApp.areBranchesPresent();

            String testFeatureAReflogContainsMain = MyApp.checkReflogForFeatureA();

            String testFeatureBReflogContainsFeatureA = MyApp.checkReflogForFeatureBRenamed();

            yakshaAssert(currentTest(), output.equals("true") && testFeatureAReflogContainsMain.equals("true") && testFeatureBReflogContainsFeatureA.equals("true"), businessTestFile);
        } catch (Exception ex) {
            yakshaAssert(currentTest(), false, businessTestFile);
        }
    }

    @Test
    @Order(2)
    public void testMergeCommitAndBranchMove() throws IOException {
        try {
            // First, check for the merge commit and branch movement for 'feature-b-renamed' -> 'feature-a'
            String mergeFeatureBRenamed = MyApp.checkMergeCommitAndBranchMove("feature-b-renamed");

            // Second, check for the merge commit and branch movement for 'feature-a' -> 'main'
            String mergeFeatureB = MyApp.checkMergeCommitAndBranchMove("feature-b");
            yakshaAssert(currentTest(), mergeFeatureBRenamed.equals("true") && mergeFeatureB.equals("true"), businessTestFile);

        } catch (Exception ex) {
            yakshaAssert(currentTest(), false, businessTestFile);
        }
    }
    
    @Test
    @Order(3)
    public void testBranchRenaming() throws IOException {
        try {
            // Check for the renaming of branch 'feature-b' to 'feature-b-renamed'
            String output = MyApp.checkBranchRenaming("feature-b", "feature-b-renamed");
            System.out.println(output);
            yakshaAssert(currentTest(), output.equals("true"), businessTestFile);

        } catch (Exception ex) {
            yakshaAssert(currentTest(), false, businessTestFile);
        }
    }
}
