package br.com.jhconsultores.sudoku.adapter

interface JogoClickedListener {

    fun infoItem (index : Int)

    fun jogoItem (index : Int)

    fun checkBoxItem (index : Int, isChecked : Boolean)

}

