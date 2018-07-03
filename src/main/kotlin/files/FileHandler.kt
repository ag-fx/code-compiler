package files

import hash.HashAlgorithm
import models.Register
import java.io.IOException
import java.io.RandomAccessFile

/**
 * Register size: 260 bytes, 64 for every String and 4 for Int
 */
class FileHandler {
    @Throws(IOException::class)
    fun write(register: Register) {
        val file = RandomAccessFile("symbol_table", "rw")
        file.seek(register.position*register.registerSize)

        writeString(register.token,file)
        writeString(register.type,file)
        file.writeInt(register.size)
        writeString(register.value,file)
        writeString(register.category,file)

        println("Written over symbol_table, Token: "+register.token+" Position: "+register.position+", Category: "+register.category)

        file.close()
    }

    private fun writeString(str: String, file: RandomAccessFile){
        for (i in 0..31) {
            if (str.length>i) file.writeChars(str[i].toString())
            else file.writeChars(' '.toString())
        }
    }

    @Throws(IOException::class)
    fun read(position: Long = 0){
        val file = RandomAccessFile("symbol_table", "rw")
        file.seek(position)
        var position = 0
        do {
            val token = readString(file)
            val type = readString(file)
            val size = file.readInt()
            val value = readString(file)
            val category = readString(file)
            if (token[0].toInt()>0){
                println("//-----Some register------//")
                println("Token: "+token)
                println("Type: "+type)
                println("Size: "+size)
                println("Value: "+value)
                println("Category: "+category)
                println("Position: "+position)
                println("File pointer: "+file.filePointer)
                App.registerList.add(Register(token,"",0,"",category, position.toLong()))
            }
            position++
        }while (file.length()>file.filePointer)
    }

    private fun readString(file: RandomAccessFile): String {
        var text = ""
        for (i in 0..31) {
            text += file.readChar()
        }
        return text
    }
}