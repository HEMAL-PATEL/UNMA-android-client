package com.paperplanes.unma.model;

/**
 * Created by abdularis on 06/12/17.
 */

public class Profile {

    public static final int USER_TYPE_STUDENT = 1;
    public static final int USER_TYPE_LECTURER = 2;

    private int mUserType;
    private String mName;
    private String mUsername;
    private ProfileClass mProfileClass;

    public Profile(int userType, String name, String username, ProfileClass profileClass) {
        mUserType = userType;
        mName = name;
        mUsername = username;
        mProfileClass = profileClass;
    }

    public int getUserType() {
        return mUserType;
    }

    public void setUserType(int userType) {
        mUserType = userType;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public ProfileClass getProfileClass() {
        return mProfileClass;
    }

    public void setProfileClass(ProfileClass profileClass) {
        mProfileClass = profileClass;
    }

    public static class ProfileClass {
        private String mStudyProgram;
        private String mClassName;
        private int mClassYear;
        private String mClassType;

        public ProfileClass(String studyProgram, String className, int classYear, String classType) {
            mStudyProgram = studyProgram;
            mClassName = className;
            mClassYear = classYear;
            mClassType = classType;
        }

        public String getStudyProgram() {
            return mStudyProgram;
        }

        public void setStudyProgram(String studyProgram) {
            mStudyProgram = studyProgram;
        }

        public String getClassName() {
            return mClassName;
        }

        public void setClassName(String className) {
            mClassName = className;
        }

        public int getClassYear() {
            return mClassYear;
        }

        public void setClassYear(int classYear) {
            mClassYear = classYear;
        }

        public String getClassType() {
            return mClassType;
        }

        public void setClassType(String classType) {
            mClassType = classType;
        }
    }

}
