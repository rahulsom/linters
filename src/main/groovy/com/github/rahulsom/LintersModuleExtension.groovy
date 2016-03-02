package com.github.rahulsom

class LintersModuleExtension {
    public static final String FORMAT_CHECKSTYLE = 'checkstyle'
    public static final String FORMAT_TEXT = 'text'

    String format = FORMAT_TEXT

    List<String> jsIncludes = ['src/assets/**/*.js']
    List<String> jsExcludes = ['src/assets/bower/**']
    List<String> htmlIncludes = ['src/assets/**/*.html']
    List<String> htmlExcludes = ['src/assets/bower/**']
    List<String> stylesIncludes = ['src/assets/**/*.less', 'src/assets/**/*.css']
    List<String> stylesExcludes = ['src/assets/bower/**']

    boolean jscs = false
    boolean jshint = false
    boolean htmlcs = false
    boolean stylelint = false

}
