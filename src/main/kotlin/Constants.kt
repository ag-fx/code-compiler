import models.SAT
import java.io.File

object Constants {
    val RESERVED_WORDS_LIST = arrayOf("class","fun","val","var","Int","String","Bool","Float","if","else","for","while","and","or","return","true","false")
    val OPERATOR_LIST = arrayOf("+","-","*","/","=",">","<","==","!=")
    val DELIMITER_LIST = arrayOf(";","{","}",":","(",")",",")
    val BOOL_LIST = arrayOf("true","false")
    val RESERVED_WORD = "PR"
    val IDENTIFIER = "ID"
    val OPERATOR = "OP"
    val DECIMAL_DIGIT = "DD"
    val INTEGER_DIGIT = "DE"
    val CHAIN_STRING = "CS"
    val COMMENT = "COM"
    val DELIMITER = "DEL"
    val BOOLEAN = "Bool"
    val INTEGER = "Int"
    val FLOAT = "Float"
    val STRING = "String"
    val NULL = "NULL"
    val CONSTANT = "Val"
    val VARIABLE = "Var"
    val LEXER_ERROR = "Lexer Error"
    val SYNTACTIC_ERROR = "Syntactic Error"
    val SEMANTIC_ERROR = "Semantic Error"
    val TOKEN_NOT_FOUND_EXCEPTION = "Token not found exception"
    val DECIMAL_FORMAT_EXCEPTION = "Decimal format exception"
    val NUMBER_FORMAT_EXCEPTION = "Number format exception"
    val COMMENT_FORMAT_EXCEPTION = "Comment format exception"
    val IDENTIFIER_FORMAT_EXCEPTION = "Identifier format exception"
    val INT_SIZE = 4
    val FLOAT_SIZE = 4
    val BOOL_SIZE = 1
    val file = File("symbols_table")

    fun initSyntacticList(){
        Mutables.syntacticList.add(SAT("E","E->TE'","","","E->TE'","",""))
        Mutables.syntacticList.add(SAT("E'","","E'->+TE'","","","E'->_","E'->_"))
        Mutables.syntacticList.add(SAT("T","T->FT'","","","T->FT'","",""))
        Mutables.syntacticList.add(SAT("T'","","T'->_","T'->*FT","","T'->_","T'->_"))
        Mutables.syntacticList.add(SAT("F","F->id","","","F->(E)'","",""))
    }
}