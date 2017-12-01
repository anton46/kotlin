/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.cli.jvm.repl

import org.jetbrains.kotlin.cli.jvm.repl.messages.*
import org.jetbrains.kotlin.cli.jvm.repl.reader.ConsoleReplCommandReader
import org.jetbrains.kotlin.cli.jvm.repl.reader.IdeReplCommandReader
import org.jetbrains.kotlin.cli.jvm.repl.reader.ReplCommandReader

interface ReplConfiguration {
    val writer: ReplWriter
    val exceptionReporter: ReplExceptionReporter
    val commandReader: ReplCommandReader
    val allowIncompleteLines: Boolean

    fun createDiagnosticHolder(): DiagnosticMessageHolder
    fun onUserCodeExecuting(isExecuting: Boolean)
}

class ReplForIdeConfiguration : ReplConfiguration {
    override val allowIncompleteLines: Boolean
        get() = false

    override fun onUserCodeExecuting(isExecuting: Boolean) {
        sinWrapper.isReplScriptExecuting = isExecuting
    }

    override fun createDiagnosticHolder() = ReplIdeDiagnosticMessageHolder()

    override val writer: ReplWriter
    override val exceptionReporter: ReplExceptionReporter
    override val commandReader: ReplCommandReader

    val sinWrapper: ReplSystemInWrapper

    init {
        // wrapper for `out` is required to escape every input in [ideMode];
        // if [ideMode == false] then just redirects all input to [System.out]
        // if user calls [System.setOut(...)] then undefined behaviour
        val soutWrapper = ReplSystemOutWrapperForIde(System.out)
        System.setOut(soutWrapper)

        // wrapper for `in` is required to give user possibility of calling
        // [readLine] from ide-console repl
        sinWrapper = ReplSystemInWrapper(System.`in`, soutWrapper)
        System.setIn(sinWrapper)

        writer = soutWrapper
        exceptionReporter = IdeReplExceptionReporter(writer)
        commandReader = IdeReplCommandReader()
    }
}

class ConsoleReplConfiguration : ReplConfiguration {
    override val allowIncompleteLines: Boolean
        get() = true

    override fun onUserCodeExecuting(isExecuting: Boolean) {
        // do nothing
    }

    override fun createDiagnosticHolder() = ReplTerminalDiagnosticMessageHolder()

    override val writer: ReplWriter
    override val exceptionReporter: ReplExceptionReporter
    override val commandReader: ReplCommandReader

    init {
        writer = ReplConsoleWriter()
        exceptionReporter = ReplExceptionReporter.DoNothing
        commandReader = ConsoleReplCommandReader()
    }
}