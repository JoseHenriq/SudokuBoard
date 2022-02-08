package br.com.jhconsultores.sudoku.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import br.com.jhconsultores.sudoku.R

// https://developer.android.com/guide/topics/ui/layout/recyclerview
class JogoAdapter(private val arLstArq   : ArrayList<String>,
                  private val arLstJogo  : ArrayList<String>,
                  private val arLstChkBox: ArrayList<Boolean>,
                  private val intVisivel : Int,
                  private val listener   : JogoClickedListener) :
                                                   RecyclerView.Adapter<JogoAdapter.ViewHolder>() {

    companion object {

        const val cTAG = "Sudoku"

    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val arqTxt  : TextView
        val jogoTxt : TextView
        val chkDel  : CheckBox

        init {

            arqTxt  = view.findViewById(R.id.card_Arq_txt)
            jogoTxt = view.findViewById(R.id.card_Jogo_txt)
            chkDel  = view.findViewById(R.id.chkBoxSelArqDel)

        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).
                                        inflate(R.layout.jogos_item, viewGroup, false)
        return ViewHolder(view)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        //----------------------------------------------------------------------
        //                        text cards
        //----------------------------------------------------------------------
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.arqTxt.text      = arLstArq   [position]
        viewHolder.jogoTxt.text     = arLstJogo  [position]
        viewHolder.chkDel.isChecked = arLstChkBox[position]

        //--- Colore o card conforme o status do jogo
        val strDado    = arLstArq [position]
        val intIdxInic = strDado.indexOf("Status: ") + 8
        val strStatus  = strDado.substring(intIdxInic)
        val cardColor : Long = if (strStatus.contains("ativo")) 0xFFA5F55C else 0xFF3D91E4

        viewHolder.arqTxt.setBackgroundColor  (cardColor.toInt())
        viewHolder.jogoTxt.setBackgroundColor (cardColor.toInt())

        //--- Declara os listeners de tap nos textos do ítem do rv
        viewHolder.arqTxt.setOnClickListener {

            Log.d(cTAG, "-> arqTxt $position" )

            //----------------------------
            listener.infoItem(position)
            //----------------------------

        }

        viewHolder.jogoTxt.setOnClickListener {

            Log.d(cTAG, "-> jogoTxt $position")

            //----------------------------
            listener.jogoItem(position)
            //----------------------------

        }

        //----------------------------------------------------------------------
        //                        delete sel checkBoxes
        //----------------------------------------------------------------------

        //--- Ativa ou desativa o checkbox do ítem conforme o status do action menu
        viewHolder.chkDel.visibility = intVisivel

        //--- Listener
        viewHolder.chkDel.setOnClickListener {

            Log.d(cTAG, "-> Del sel checkBox $position")

            //-------------------------------------------------------------
            listener.checkBoxItem(position, viewHolder.chkDel.isChecked)
            //-------------------------------------------------------------

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = arLstArq.size

}
