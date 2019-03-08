
NumpadAdd::
  l := readline()
  Send, {Ctrl down}a{Ctrl up}
  Send, %l%
  Sleep, 1000
  Send, {Enter}
return


RemoveLines(filename, startingLine, numOfLines){
       Loop, Read, %filename%
               if ( A_Index < StartingLine )
                       || ( A_Index >= StartingLine + numOfLines )
                               ret .= "`r`n" . A_LoopReadLine
       FileDelete, % FileName
       FileAppend, % SubStr(ret, 3), % FileName
}

readline2(){
  ;Read First line of file
    FileReadLine, line, %A_WorkingDir%/Barcodes - kopia.txt, 1

    ;Deletes First Line
    fileread, text_list, %A_WorkingDir%/Barcodes - kopia.txt
    RegExMatch(text_list, ".+?\v", oVar)                                   ; stores first line in oVar
    text_list := RegExReplace(text_list, ".+?\v", "", "", 1)          ; deletes the first line and stores what's left in the var text_list
    filedelete, %A_WorkingDir%/Barcodes - kopia.txt
    fileappend, %text_list%, %A_WorkingDir%/Barcodes - kopia.txt   ;recreates the file

    ;Clean Up - Empty Spaces
    File = Barcodes - kopia.txt                                                            ;file name here
    FileRead, text, %File%
    FileDelete, %File%
    FileAppend, % SubStr(text, 2), %File%
    return line
}


readline(){
  FileReadLine, line, %A_WorkingDir%/Barcodes - kopia.txt, 1
  RemoveLines("Barcodes - kopia.txt", 1, 1)
  return line
}
