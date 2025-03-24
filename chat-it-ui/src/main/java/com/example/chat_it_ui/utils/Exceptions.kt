package com.example.chat_it_ui.utils

class ModuleNotFoundException(moduleName: String) : Exception("$moduleName module not found, have you added the $moduleName dependency correctly in build.gradle.kts?")