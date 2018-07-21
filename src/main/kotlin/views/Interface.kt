package views

import Constants
import Mutables
import analyzers.LexerAnalyzer
import analyzers.SyntacticAnalyzer
import files.FileHandler
import files.SyntacticTableFile
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.paint.Color
import models.Register
import models.SAT
import models.SyntacticDebugTable
import tornadofx.*
import kotlin.concurrent.thread

class Interface: View() {
    override val root = borderpane {
        left<EditorView>()
        right<SymbolsTable>()
        bottom<ErrorsView>()
    }
}

class EditorView : View() {
    private val controller: MyController by inject()
    private val errorView: ErrorsView by inject()
    private val tableView: SymbolsTable by inject()
    private val editorInput = SimpleStringProperty()
    val syntacticObservables = FXCollections.observableArrayList<SyntacticDebugTable>()!!
    var outputStr = ""

    override val root = form {

        //Fill the syntactic analysis table
        runAsync {
            Mutables.syntacticTableList.clear()
            controller.fillSyntacticTable()
        } ui {
            val syntacticList = Mutables.syntacticTableList.observable()
            tableView.syntacticList.asyncItems { syntacticList }
        }

        fieldset {
            button("Compilar") {
                action {
                    progressbar {
                        thread {
                            for (i in 1..100) {
                                Platform.runLater { progress = i.toDouble() / 100.0 }
                                Thread.sleep(10)
                            }

                            controller.makeAnalysis(editorInput.value.split("\n"))

                            //Debugging for errors output
                            outputStr = ""
                            runAsync {
                                Mutables.errorList.forEach { item ->
                                    outputStr += item.init+" "+item.errorType+", '"+item.token+"' "+item.description+", line "+item.line+"\n"
                                }
                            } ui {
                                errorView.errorOutput.value = outputStr
                                println("ERROR_LIST_SIZE: "+Mutables.errorList.size)
                                Mutables.errorList.clear()
                            }

                            //Fill the lexer analysis table
                            runAsync {
                                Mutables.lexerTableList.clear()
                                controller.readFromFile()
                            } ui {
                                val registerList = Mutables.lexerTableList.observable()
                                tableView.registerList.asyncItems { registerList }
                            }

                            //Debugging for syntactic debug table
                            outputStr = ""
                            runAsync {
                                Mutables.syntacticDebugList.clear()
                                controller.makeSyntacticAnalysis()
                            } ui {
                                val syntacticAnalysis = Mutables.syntacticDebugList.observable()
                                syntacticObservables.asyncItems { syntacticAnalysis }
                            }
                        }
                    }
                }
            }

            label("Editor") {
                textFill = Color.BLUE
            }
            field {
                textarea(editorInput)
                //textarea(input).text = "& er4 sds // %$ Int class main { fun test ( ) : Int { Int value = 5 + 3 ; Float value = 5.5 ; if ( value < value2 ) { value = 20 ; } return value } }"
            }

            label("Syntactic debugger") {
                textFill = Color.BROWN
            }
            maxHeight = 500.0
            tableview(syntacticObservables) {
                readonlyColumn("Pile",SyntacticDebugTable::pile).minWidth = 150.0
                readonlyColumn("Input",SyntacticDebugTable::input).minWidth = 400.0
            }
        }

    }
}

class MyController: Controller() {
    private fun makeLexerAnalysis(inputValue: List<String>){
        val lexerAnalyzer = LexerAnalyzer()
        lexerAnalyzer.analyzeLine(inputValue)
    }
    fun makeSyntacticAnalysis(){
        val syntacticAnalysis = SyntacticAnalyzer()
        syntacticAnalysis.makeSyntacticAnalysis()
    }
    fun readFromFile(){
        val fileHandler = FileHandler()
        fileHandler.read()
    }
    fun fillSyntacticTable(){
        val syntacticFile = SyntacticTableFile()
        syntacticFile.getData()
    }
    private fun initCodeList(){
        Mutables.codeList.push(Register("$","",0,"","",0,""))
    }
    fun makeAnalysis(inputValue: List<String>){
        Constants.file.delete()
        initCodeList()
        makeLexerAnalysis(inputValue)
    }
}

class ErrorsView: View() {
    val errorOutput = SimpleStringProperty()

    override val root = form {

        fieldset{
            label("Errors") {
                textFill = Color.RED
            }
            field {
                textarea(errorOutput).maxHeight = 150.0
            }
        }
    }
}

class SymbolsTable: View() {
    val registerList = FXCollections.observableArrayList<Register>()!!
    val syntacticList = FXCollections.observableArrayList<SAT>()!!

    override val root = form {

        style { setMaxSize(802.0,500.0) }

        label("Symbols table") {
            textFill = Color.BROWN
        }
        tableview(registerList) {
            readonlyColumn("Token",Register::token).minWidth = 300.0
            readonlyColumn("Type",Register::type)
            readonlyColumn("Size",Register::size)
            readonlyColumn("Value",Register::value)
            readonlyColumn("Category",Register::category)
            readonlyColumn("Position",Register::position)
            readonlyColumn("Typing",Register::typing)
        }

        label("Syntactic analysis table") {
            textFill = Color.BROWN
        }
        tableview(syntacticList) {
            readonlyColumn("name",SAT::s)
            readonlyColumn("id",SAT::id)
            readonlyColumn("+",SAT::plus)
            readonlyColumn("*",SAT::by)
            readonlyColumn("(",SAT::opPar)
            readonlyColumn(")",SAT::clPar)
            readonlyColumn("$",SAT::sign)
        }
    }
}