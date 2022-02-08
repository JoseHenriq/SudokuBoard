package br.com.jhconsultores.model

/* Projeto Agenda Básica (todoList / DIO)

data class Task(

    val id   : Int = 0,
    val title: String,
    val date : String,
    val hour : String ) {

    //--- Verifica se a lista já contém uma task como a recebida
    override fun equals(other: Any?): Boolean {

        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as Task

        if (id != other.id) return false

        return true
    }

    //--- Gera um hashcode para a task
    override fun hashCode(): Int {
        return id
    }
}
*/

data class Jogo(

    val id : Int = 0,

    val itemsListArq    : ArrayList<String>,
    val itemsListJogo   : ArrayList<String>,
    val itemsListChkDel : ArrayList<Boolean>,
    val intVisibilidde  : Int ) {

    //--- Verifica se a lista já contém um jogo como o recebido
    override fun equals(other: Any?): Boolean {

        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as Jogo

        if (id != other.id) return false

        return true
    }

    //--- Gera um hashcode para a task
    override fun hashCode(): Int {

        return id

    }

}




