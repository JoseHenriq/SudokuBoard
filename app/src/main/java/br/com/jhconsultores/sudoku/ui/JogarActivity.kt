package br.com.jhconsultores.sudoku.ui

import android.graphics.*
import android.widget.*

import android.content.Context

import android.util.Log
import android.util.TypedValue

import android.view.View

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import br.com.jhconsultores.sudoku.R
import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.arStrNivelJogo
import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.flagMostraNumIguais
import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.intLimiteErros
import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.strLimiteTempo
import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.strOpcaoJogo
import br.com.jhconsultores.utils.*

import java.lang.Exception

class JogarActivity : AppCompatActivity() {

    //--------------------------------------------------------------------------
    //                    Instancializações e inicializações
    //--------------------------------------------------------------------------
    private var cTAG = "Sudoku"
    private var strLog = ""
    private var strToast = ""

    companion object {

        private var strModelo: String = ""
        private var intIdxFim: Int = 0

    }

    //--- ImageView
    private var iViewSudokuBoard: ImageView? = null
    private var iViewNumsDisps: ImageView? = null

    //--- Bmp
    private var intImageResource = 0

    //private var bmpInic: Bitmap? = null // Board vazio Lido a partir do resource/drawable
    private var bmpSalvaJogo: Bitmap? = null // salva o bmpMyImage que é o bmp do jogo

    //private var bmpSudokuBoard: Bitmap? = null         // Jogo
    private var bmpJogo: Bitmap? = null         // Preset ou jogo gerado e novos números
    private var bmpNumDisp: Bitmap? = null         // Números disponíveis

    //private lateinit var bmpJogoSemCandidatos: Bitmap
    //private var flagEscCandJogos = false

    //--- Canvas
    //private var canvasSudokuBoard: Canvas? = null
    private var canvasJogo: Canvas? = null
    private var canvasNumDisp: Canvas? = null

    private var tvNivel: TextView? = null
    private var tvSubNivel: TextView? = null
    private var tvClues: TextView? = null
    private var tvLegCluesInic: TextView? = null
    private var tvCluesInic: TextView? = null

    private var tvErros: TextView? = null
    private var intContaErro = 0

    private lateinit var toolBar: androidx.appcompat.widget.Toolbar

    private var intTamTxt = 25 // 50 // 200 //
    private var scale = 0f

    private var pincelVerde = Paint()
    private var pincelBranco = Paint()
    private var pincelPreto = Paint()
    private var pincelAzul = Paint()
    private var pincelLaranja = Paint()
    private var pincelPurple200 = Paint()

    //--- Medidas
    //- Células do board
    private var intCellwidth = 0
    private var intCellheight = 0

    //- Margens do board
    private var intmargTopDp = 0
    private var intMargleftdp = 0
    private var intMargtoppx = 0
    private var intMargleftpx = 0

    //- Imagem Sudoku board
    private var intImgwidth = 0
    private var intImgheight = 0

    //--- Controle de jogadas
    private var intColJogar = 0
    private var intLinJogar = 0
    private var flagJoga = false

    private var arIntNumsJogo = ArrayList<Int>()                      // Jogo
    var arArIntNums = Array(9) { Array(9) { 0 } }
    private var arArIntCopia = Array(9) { Array(9) { 0 } }
    private var arArIntCand = Array(81) { Array(9) { 0 } }
    private var arArSalvaJogo = Array(9) { Array(9) { 0 } }

    private var arIntNumsGab = ArrayList<Int>()                       // Gabarito
    private var arArIntGab = Array(9) { Array(9) { 0 } }

    private var arIntNumsDisp = Array(9) { 9 }                   // "caixinha"

    private var action = "JogoGerado"

    private var strNivelJogo = "Fácil"
    private var strSubNivelJogo = "0"
    private var strNivelJogoInic = "Fácil"
    private var strSubNivelJogoInic = "0"

    private lateinit var crono: Chronometer
    private var strCronoInic = ""
    private var strCronoInicIntent = ""

    private var intContaErroInic = 0

    //--- Buttons
    private lateinit var btnInicia: Button
    private lateinit var btnCand: Button
    private lateinit var btnReset: Button
    private lateinit var btnSalvar: Button

    private var strInicia = ""
    private var strPause = ""
    private var strReInicia = ""

    private lateinit var ctimer: CountDownTimer
    private var timeOut = (10 * 60 * 60 * 1000).toLong()   // 10'
    private var timeTick = 1000                             //  1"

    private var strFinalJogo = ""

    //--- Classes externas
    private val utils = Utils()
    private val utilsKt = UtilsKt()

    //--------------------------------------------------------------------------
    //                                Eventos
    //--------------------------------------------------------------------------
    //--- onCreate MainActivity
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        var flagAppInicializado = false
        try {
            setContentView(R.layout.activity_jogar)

            tvLegCluesInic = findViewById(R.id.tv_LegCluesInic)
            tvCluesInic = findViewById(R.id.tv_CluesInic)

            tvLegCluesInic!!.visibility = INVISIBLE
            tvCluesInic!!.visibility = INVISIBLE

            //--- Cronômetro
            strCronoInic = resources.getString(R.string.crono_inic)
            //---------------------------------
            crono = Chronometer(this)
            //---------------------------------
            preparaCrono(crono)
            //--------------------

            //--- Erros
            tvErros = findViewById(R.id.tv_Erros)
            tvErros!!.text = "0"

            //--- Instancia objetos locais para os objetos XML
            tvNivel = findViewById(R.id.tv_Nivel)
            tvSubNivel = findViewById(R.id.tv_Subnivel)
            tvClues = findViewById(R.id.tv_Clues)

            btnReset = findViewById<View>(R.id.btnReset) as Button
            btnSalvar = findViewById<View>(R.id.btnSalvar) as Button
            btnInicia = findViewById<View>(R.id.btnInicia) as Button
            btnCand = findViewById<View>(R.id.btnCand) as Button
            btnInicia.isEnabled = true

            //--- Implementa o actionBar
            toolBar = findViewById(R.id.toolbar2)
            toolBar.title = MainActivity.strApp + " - Jogo"

            setSupportActionBar(toolBar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            //--------------------------------------------------------------------------------------
            // Objetos gráficos
            //--------------------------------------------------------------------------------------

            //------------------------
            inicializaObjGraficos()
            //------------------------

            //------------------------------------------------------------------
            // Listeners para o evento onTouch dos ImageViews
            //------------------------------------------------------------------
            iViewSudokuBoard!!.setOnTouchListener { _, event -> //--- Coordenadas tocadas

                if (btnInicia.text == "Inicia" || btnInicia.text == "ReInicia") {

                    utilsKt.mToast(this, "Jogo não iniciado!")

                } else {

                    try {
                        //--- Determina linha e coluna tocada
                        val x = event.x.toInt()
                        val y = event.y.toInt()
                        //Log.d(cTAG, "touched x: $x touched y: $y")

                        //------------------------------
                        selecionaCelSudokuBoard(x, y)
                        //------------------------------

                    } catch (exc: Exception) {

                        strLog = "Erro iViewSudokuBoard: ${exc.message}"
                        Log.d(cTAG, strLog)

                        Toast.makeText(this, strLog, Toast.LENGTH_SHORT).show()

                    }

                }
                false

            }

            // Números Disponíveis para se colocar em jogo
            iViewNumsDisps!!.setOnTouchListener { _, event -> //--- Só transfere o número para o board se estiver jogando

                if (btnInicia.text == "Inicia" || btnInicia.text == "ReInicia") {

                    utilsKt.mToast(this, "Jogo não iniciado!")

                } else {

                    try {

                        if (flagJoga) {

                            //--- Determina coluna do numsDisps tocada
                            val x = event.x.toInt()

                            //--- Coordenada X e valor da célula tocada
                            val intCol = x / intCellwidth

                            //strLog = "-> Celula tocada em NumDisp: coluna = " + intCol +
                            //        ", qtidd = " + intQtidd
                            //Log.d(cTAG, strLog)

                            //----------------------------
                            selecionaCelNumDisp(intCol)
                            //----------------------------
                        }

                    } catch (exc: Exception) {

                        strLog = "Erro iViewNumsDisps: ${exc.message}"
                        Log.d(cTAG, strLog)

                        Toast.makeText(this, strLog, Toast.LENGTH_SHORT).show()

                    }
                }

                false

            }

            //------------------------------------------------------------------
            // Listeners para o evento onClick dos buttons
            //------------------------------------------------------------------


            //--- btnInicia
            strInicia = resources.getString(R.string.inicia)
            strPause = resources.getString(R.string.pause)
            strReInicia = resources.getString(R.string.reinicia)

            btnInicia.setOnClickListener {

                //--------------------
                btnIniciaListener()
                //--------------------

            }

            //--- Botão Candidatos
            // Commit sudoku_#9.0.178
            //--- Botão para análise dos candidatos
            btnCand.setOnClickListener {

                strLog = "-> Tap no btn \"${btnCand.text}\" "
                Log.d(cTAG, strLog)

                //--- Análise de candidatos
                when (btnCand.text) {

                    "Candidatos" -> {

                        //--------------------
                        analisaCandidatos()
                        //--------------------
                        btnCand.text = "Volta Jogo"

                    }
                    //--- Retorna ao jogo
                    "Volta Jogo" -> {

                        //--------------
                        retornaJogo()
                        //--------------
                        btnCand.text = "Candidatos"

                    }
                    //--- Não faz nada
                    else -> {
                    }

                }

            }

            //--- Botão de Reset
            btnReset.setOnClickListener {

                strLog = "-> Tap no btn \"${btnReset.text}\" "
                Log.d(cTAG, strLog)

                //--- Solicita ao usuário a confirmação do reset
                androidx.appcompat.app.AlertDialog.Builder(this)

                    .setTitle("Sudoku - Jogo")
                    .setMessage("Tem certeza que quer recomeçar o jogo atual?")

                    .setPositiveButton("Sim") { _, _ ->

                        Log.d(cTAG, "-> \"Sim\" was pressed")

                        tvNivel!!.text = ""
                        tvSubNivel!!.text = ""

                        intContaErro = intContaErroInic
                        tvErros!!.text = "$intContaErro"

                        Log.d(cTAG, "-> ${crono.text} - Reset")

                        strCronoInic = strCronoInicIntent
                        //--------------------------
                        acertaCrono(strCronoInic)
                        //--------------------------

                        //-------------------------------------------------
                        arArIntNums = utilsKt.copiaArArInt(arArIntCopia)
                        //-------------------------------------------------

                        arIntNumsDisp =
                            Array(9) { 9 }     // intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)

                        //-------------
                        iniciaJogo()
                        //-------------

                        btnInicia.isEnabled = true
                        btnInicia.text = resources.getString(R.string.inicia)

                        btnCand.text = "Candidatos"

                    }

                    .setNegativeButton("Não") { _, _ ->

                        Log.d(cTAG, "-> \"Não\" was pressed")

                    }

                    .show()

            }

            //--- Botão de Salvar o jogo
            btnSalvar.setOnClickListener {

                strLog = "-> Tap no btn \"Salvar\" "
                Log.d(cTAG, strLog)

                androidx.appcompat.app.AlertDialog.Builder(this)

                    .setTitle("Sudoku - Jogo")
                    .setMessage("Tem certeza que quer salvar o jogo?")

                    .setPositiveButton("Sim") { _, _ ->

                        Log.d(cTAG, "-> \"Sim\" was pressed")

                        //============
                        salvaJogo()
                        //============

                        Log.d(cTAG, "-> Jogos salvos: ")
                        //-----------------------------------------------------------------------
                        val arStrArqsNames = utils.listaExtMemArqDir("/sudoku/Jogos")
                        //-----------------------------------------------------------------------
                        if (arStrArqsNames.isNotEmpty()) {

                            for (strArqName in arStrArqsNames) {
                                Log.d(cTAG, "   - $strArqName")
                            }

                        } else {

                            strLog =
                                "   - Não há arquivos de jogos no dir /Download/sudoku/Jogos"
                            Log.d(cTAG, strLog)

                        }

                    }

                    .setNegativeButton("Não") { _, _ ->

                        Log.d(cTAG, "-> \"Não\" was pressed")

                    }
                    .show()

            }

            //--------------------------------------------------------------------------------------
            // Dados enviados pelo MainActivity ou pelo AdaptarActivity
            //--------------------------------------------------------------------------------------
            //--- Ação à ser executada
            action = intent.action.toString()        // Main:

            //--- Recupera os dados recebidos via intent
            strNivelJogoInic = intent.getStringExtra("strNivelJogo") as String
            strSubNivelJogoInic = intent.getStringExtra("strSubNivelJogo") as String

            strCronoInic = intent.getStringExtra("strCronoConta") as String
            intContaErroInic = (intent.getStringExtra("strErro") as String).toInt()

            // Armazena o gabarito em um array<int>
            arIntNumsGab = intent.getIntegerArrayListExtra("GabaritoDoJogo") as ArrayList<Int>
            // Armazena o jogo em um array<int>
            arIntNumsJogo = intent.getIntegerArrayListExtra("JogoPreparado") as ArrayList<Int>

            //--- Verifica a consistência dos dados recebidos

            // Gabarito e/ou 7jogo inválidos
            if (arIntNumsGab.size != 81 || arIntNumsJogo.size != 81) {

                Log.d(cTAG, "-> Erro: array(s) com menos numeros que o necessário (81)")

            }
            // Gabarito e jogo válidos
            else {

                //--- Prepara a estrutura de dados para o jogo
                strCronoInicIntent = strCronoInic

                //--- Acerta crono
                //--------------------------
                acertaCrono(strCronoInic)
                //--------------------------

                tvErros!!.text = intContaErroInic.toString()

                // Armazena o gabarito e o jogo em Array<Array<Int>> para processamento local
                for (intLinha in 0..8) {

                    for (intCol in 0..8) {

                        val intCell = intLinha * 9 + intCol
                        arArIntNums[intLinha][intCol] = arIntNumsJogo[intCell]  // Jogo
                        arArIntGab[intLinha][intCol] = arIntNumsGab[intCell]   // Gabarito

                    }
                }

                //--- Salva o rascunho do jogo recebido
                //-------------------------------------------------
                arArIntCopia = utilsKt.copiaArArInt(arArIntNums)
                //-------------------------------------------------

                arIntNumsDisp = Array(9) { 9 }

                if (action.contains("JogoPresetado")) {

                    //--- Apresenta as infos do jogo carregado (atuais)
                    val qtiddZeros = utilsKt.quantZeros(arArIntNums)
                    // Limita o índice de 0 à 3 (Fácil, Médio, Difícil e Muito difícil)
                    //val idxNivel   = if ((qtiddZeros / 10 - 2) < 0) 0 else
                    //                  (if ((qtiddZeros / 10 - 2) > 3) 3 else (qtiddZeros / 10 - 2))

                    val idxNivel = when {

                        (qtiddZeros / 10 - 2) < 0 -> {
                            strSubNivelJogo = "0"
                            0
                        }
                        (qtiddZeros / 10 - 2) < 4 -> {
                            strSubNivelJogo = (qtiddZeros % 10).toString()
                            (qtiddZeros / 10 - 2)
                        }
                        else -> {

                            strSubNivelJogo = "0"
                            3
                        }

                    }

                    strNivelJogo = arStrNivelJogo[idxNivel]

                    // ex: Fácil de 20 a 29
                    tvNivel!!.text = strNivelJogo
                    tvSubNivel!!.text = strSubNivelJogo

                    //--- Apresenta as info do jogo inicial
                    tvLegCluesInic!!.text = " ( Nível inicial: "
                    tvCluesInic!!.text = String.format(
                        "%s / %s )", strNivelJogoInic,
                        strSubNivelJogoInic
                    )

                    if ((strNivelJogo != strNivelJogoInic) || (strSubNivelJogo != strSubNivelJogoInic)) {

                        tvLegCluesInic!!.visibility = VISIBLE
                        tvCluesInic!!.visibility = VISIBLE

                    } else {

                        tvLegCluesInic!!.visibility = INVISIBLE
                        tvCluesInic!!.visibility = INVISIBLE

                    }

                }
                //--- "JogoGerado"
                else {

                    strNivelJogo = strNivelJogoInic
                    strSubNivelJogo = strSubNivelJogoInic

                }

                tvNivel!!.text = strNivelJogo
                tvSubNivel!!.text = strSubNivelJogo

                flagAppInicializado = true

            } // Fim de gabarito recebido via intent ok

        } catch (exc: Exception) {

            Log.d(cTAG, "Erro: ${exc.message}")

        }

        //--------------------------------------------------------------------------------------
        // Se inicialização OK: inicializa o Jogo
        //--------------------------------------------------------------------------------------
        if (flagAppInicializado) {

            //-------------
            iniciaJogo()
            //-------------

        }

    }

    //----------------------------------------------------------------------------------------------
    //                                     Funções
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    // Gráficas - Sudoku Board
    //----------------------------------------------------------------------------------------------
    //--- desenhaSudokuBoard
    private fun desenhaSudokuBoard(flagApaga: Boolean) {

        var flCoordXInic: Float
        var flCoordYInic: Float
        var flCoordXFim: Float
        var flCoordYFim: Float
        val flPincelFino = 2.toFloat()
        val flPincelGrosso = 6.toFloat()
        val pincelDesenhar = pincelPreto

        val flagApagaBoard: Boolean = flagApaga

        //--- Redesenha o board a partir do zero
        //flagApagaBoard = true
        if (flagApagaBoard) {

            //-----------------------------------------------------------------------------
            canvasJogo!!.drawRect(
                0f,
                0f,
                intImgwidth.toFloat(),
                intImgheight.toFloat(),
                pincelBranco
            )
            //-----------------------------------------------------------------------------
        }

        //--- Desenha as linhas horizontais
        for (intLinha in 0..9) {
            flCoordXInic = 0f
            flCoordYInic = (intLinha * intCellheight).toFloat()

            flCoordXFim = (9 * intCellwidth).toFloat()
            flCoordYFim = flCoordYInic

            var flLargPincel = flPincelFino
            if (intLinha % 3 == 0) {
                flLargPincel = flPincelGrosso
                if (flCoordYInic > 0) flCoordYInic--
                if (flCoordYFim > 0) flCoordYFim--
            }
            pincelDesenhar.strokeWidth = flLargPincel
            //--------------------------------------------------------------------------------------
            canvasJogo!!.drawLine(
                flCoordXInic, flCoordYInic, flCoordXFim, flCoordYFim, pincelDesenhar
            )
            //--------------------------------------------------------------------------------------
        }
        //--- Desenha as linhas verticais
        for (intCol in 0..9) {
            flCoordXInic = (intCol * intCellwidth).toFloat()
            flCoordYInic = 0f
            flCoordXFim = flCoordXInic
            flCoordYFim = (9 * intCellheight).toFloat()
            var flLargPincel = flPincelFino
            if (intCol % 3 == 0) {
                flLargPincel = flPincelGrosso
                if (flCoordXInic > 0) flCoordXInic--
                if (flCoordXFim > 0) flCoordXFim--
            }

            pincelDesenhar.strokeWidth = flLargPincel
            //--------------------------------------------------------------------------------------
            canvasJogo!!.drawLine(
                flCoordXInic, flCoordYInic, flCoordXFim, flCoordYFim,
                pincelDesenhar
            )
            //--------------------------------------------------------------------------------------
        }

    }

    //--- mostraNumsIguais (canvas do Sudoku board)
    private fun mostraNumsIguais(intNum: Int, flagMostraNumsIguais: Boolean) {

        if (flagMostraNumIguais) {

            //--- Atualiza a imageView do layout
            utilsKt.copiaBmpByBuffer(bmpSalvaJogo, bmpJogo)
            //--------------------------------------------------------
            iViewSudokuBoard!!.setImageBitmap(bmpJogo)
            //----------------------------------------------
            for (intLin in 0..8) {

                for (intColuna in 0..8) {

                    if (arArIntNums[intLin][intColuna] == intNum) {

                        //--- Pinta a célula
                        //------------------------------------------------
                        pintaCelula(intLin, intColuna, pincelPurple200)
                        //------------------------------------------------

                        //--- Escreve o número na célula
                        pincelBranco.textSize = intTamTxt * scale
                        //------------------------------------------------------------------
                        escreveCelula(intLin, intColuna, intNum.toString(), pincelBranco)
                        //------------------------------------------------------------------
                    }
                }
            }

            //--- Atualiza a imageView do layout

            // Commit9.0.178
            //--- Se estiver mostrando os candidatos, atualiza-os.
            if (btnCand.text == "Volta Jogo") {

                //utilsKt.copiaBmpByBuffer(bmpSalvaJogo, bmpJogoSemCandidatos)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    //--------------------
                    analisaCandidatos()
                    //--------------------

                }
            }

            //-------------------------------------------
            iViewSudokuBoard!!.setImageBitmap(bmpJogo)
            //-------------------------------------------
        } else {

            if (flagMostraNumsIguais) {

                Toast.makeText(
                    this, "Configurado para NÃO mostrar números iguais!",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

    }

    //--- mostraCelAJogar
    private fun mostraCelAJogar(intLin: Int, intColuna: Int) {

        //--- Atualiza a imageView do layout
        //------------------------------------------------
        utilsKt.copiaBmpByBuffer(bmpSalvaJogo, bmpJogo)
        //------------------------------------------------
        iViewSudokuBoard!!.setImageBitmap(bmpJogo)
        //-------------------------------------------

        //--- Pintura da celula
        //----------------------------------------------
        pintaCelula(intLin, intColuna, pincelLaranja)
        //----------------------------------------------

        //--- Atualiza a imageView do layout
        //-------------------------------------------
        iViewSudokuBoard!!.setImageBitmap(bmpJogo)
        //-------------------------------------------

    }

    //----------------------------------------------------------------------------------------------
    // Gráficas - numDisp
    //----------------------------------------------------------------------------------------------
    //--- atualiza numDisp (canvasNumDisp)
    private fun atualizaNumDisp() {

        val intOffSet = 3

        //--- Pinta as células
        for (intIdxCel in 0..8) {

            //--- Coordenadas da célula
            //- Canto superior esquerdo do quadrado
            val intXSupEsq = intIdxCel * intCellwidth + intOffSet
            //- Canto inferior direito do quadrado
            val intXInfDir = intXSupEsq + intCellwidth - intOffSet
            val intYInfDir = intCellheight - intOffSet
            //----------------------------------------
            val intQtidd = arIntNumsDisp[intIdxCel]
            //----------------------------------------
            val pincelPintar = if (intQtidd > 0) pincelVerde else pincelBranco
            try {
                //-----------------------------------------------------------------------------------
                canvasNumDisp!!.drawRect(
                    intXSupEsq.toFloat(),
                    intOffSet.toFloat(),
                    intXInfDir.toFloat(),
                    intYInfDir.toFloat(),
                    pincelPintar
                )
                //-----------------------------------------------------------------------------------
            } catch (exc: Exception) {
                Log.d(cTAG, "Erro: " + exc.message)
            }

            //--- Desenha as linhas de separação
            val xSupDir = intXInfDir.toFloat()
            val ySupDir = intOffSet.toFloat()
            pincelPreto.strokeWidth = 5.toFloat()
            //----------------------------------------------------------------------------------
            canvasNumDisp!!.drawLine(
                xSupDir, ySupDir, intXInfDir.toFloat(), intYInfDir.toFloat(),
                pincelPreto
            )
            //----------------------------------------------------------------------------------

            //--- Escreve os números ainda válidos para o jogo
            if (intQtidd > 0) {

                //--- Escreve o número na célula
                val yCoord = intCellheight * 3 / 4
                val xCoord = intCellwidth / 3 + intIdxCel * intCellwidth

                val strTxt = (intIdxCel + 1).toString()
                pincelBranco.textSize = intTamTxt * scale
                //----------------------------------------------------------------------------------
                canvasNumDisp!!.drawText(
                    strTxt,
                    xCoord.toFloat(),
                    yCoord.toFloat(),
                    pincelBranco
                )
                //----------------------------------------------------------------------------------

            }
        }

        //--- Atualiza a imageView do layout
        //--------------------------------------------
        iViewNumsDisps!!.setImageBitmap(bmpNumDisp)
        //--------------------------------------------

    }

    //----------------------------------------------------------------------------------------------
    // Gráficas - funções comuns aos canvas
    //----------------------------------------------------------------------------------------------
    //--- pintaCelula no canvasMyImage
    private fun pintaCelula(intLinha: Int, intCol: Int, pincelPintar: Paint?) {

        //- Canto superior esquerdo do quadrado
        val intXSupEsq = intCol * intCellwidth
        val intYSupEsq = intLinha * intCellheight
        //- Canto inferior direito do quadrado
        val intXInfDir = intXSupEsq + intCellwidth
        val intYInfDir = intYSupEsq + intCellheight
        //-------------------------------------------------------------------------------
        canvasJogo!!.drawRect(
            intXSupEsq.toFloat(),
            intYSupEsq.toFloat(),
            intXInfDir.toFloat(),
            intYInfDir.toFloat(),
            pincelPintar!!
        )
        //-------------------------------------------------------------------------------
        desenhaSudokuBoard(false)
        //-----------------------------------

    }

    //--- escreveCelula no canvasMyImage
    private fun escreveCelula(yCell: Int, xCell: Int, strTxt: String, pincel: Paint?) {

        //--- Coordenada Y (linhas)
        //------------------------------------------------------------------
        val yCoord = intCellheight * 3 / 4 + yCell * intCellheight
        //------------------------------------------------------------------

        //--- Coordenada X (colunas)
        //----------------------------------------------------------
        val xCoord = intCellwidth / 3 + xCell * intCellwidth
        //----------------------------------------------------------
        //strLog = "-> Coordenadas (Col = " + xCell + ", Linha: " + yCell + ") : (" + xCoord +
        //        ", " + yCoord + ") = " + strTxt
        //Log.d(cTAG, strLog)

        //----------------------------------------------------------------------------
        canvasJogo!!.drawText(strTxt, xCoord.toFloat(), yCoord.toFloat(), pincel!!)
        //----------------------------------------------------------------------------

        //--- Atualiza a imageView do layout
        //----------------------------------------------
        iViewSudokuBoard!!.setImageBitmap(bmpJogo)
        //----------------------------------------------

    }

    //--- inicializaObjGraficos
    @RequiresApi(Build.VERSION_CODES.R)
    private fun inicializaObjGraficos() {

        //--- Grandezas gráficas
        // scale = resources.displayMetrics.density

        //--- Pincéis
        pincelVerde.color = ContextCompat.getColor(this, R.color.verde)
        pincelBranco.color = ContextCompat.getColor(this, R.color.white)
        pincelPreto.color = ContextCompat.getColor(this, R.color.black)
        pincelAzul.color = ContextCompat.getColor(this, R.color.azul)
        pincelLaranja.color = ContextCompat.getColor(this, R.color.laranja)
        pincelPurple200.color = ContextCompat.getColor(this, R.color.purple_200)

        //--- Bit maps
        intImageResource = R.drawable.sudoku_board3

        //--- Jogo
        bmpJogo = BitmapFactory.decodeResource(resources, intImageResource)
            .copy(Bitmap.Config.ARGB_8888, true)
        canvasJogo = Canvas(bmpJogo!!)
        iViewSudokuBoard = findViewById<View>(R.id.ivSudokuBoard) as ImageView
        iViewSudokuBoard!!.setImageBitmap(bmpJogo)

        bmpSalvaJogo = BitmapFactory.decodeResource(resources, intImageResource)
            .copy(Bitmap.Config.ARGB_8888, true)

        //bmpJogoSemCandidatos = BitmapFactory.decodeResource(resources, intImageResource)
        //    .copy(Bitmap.Config.ARGB_8888, true)

        //--- Números disponíveis (caixinha)
        bmpNumDisp = BitmapFactory.decodeResource(resources, R.drawable.quadro_nums_disp)
            .copy(Bitmap.Config.ARGB_8888, true)
        canvasNumDisp = Canvas(bmpNumDisp!!)
        iViewNumsDisps = findViewById<View>(R.id.ivNumDisp) as ImageView
        iViewNumsDisps!!.setImageBitmap(bmpNumDisp)

        //bmpInic = BitmapFactory.decodeResource(resources, intImageResource)
        //    .copy(Bitmap.Config.ARGB_8888, true)
        //bmpSudokuBoard = BitmapFactory.decodeResource(resources, intImageResource)
        //    .copy(Bitmap.Config.ARGB_8888, true)
        //canvasSudokuBoard = Canvas(bmpSudokuBoard!!)

        //-----------------------------
        determinaGrandezasGraficas()
        //-----------------------------

    }

    //--- apresentaGrandezasGraficas
    @RequiresApi(Build.VERSION_CODES.R)
    private fun determinaGrandezasGraficas() {

        //--- Determina o fator de escala do screen do device
        scale = resources.displayMetrics.density

        //--- Apresenta as principais grandezas gráficas do device
        //2021-11-29 17:03:42.169 D: -> Display: Largura: 720 pixels, Altura  : 1280 pixels
        //2021-11-29 17:03:42.171 D: -> Margens: Acima  :  20 pixels, Esquerda:    0 pixels
        //2021-11-29 17:03:42.172 D: -> Image  : Largura: 720 pixels, Altura  :  630 pixels
        //2021-11-29 17:03:42.172 D: -> Célula : Largura:  80 pixels, Altura  :   70 pixels
        try {

            //--- Dimensões do display do Samsung SM-G570M
            /* Código deprecated no Android 30 (R)
			Display display = getWindowManager().getDefaultDisplay();
			Point m_size    = new Point();
			display.getSize(m_size);
			int int_dyWidth  = m_size.x;
			int int_dyHeight = m_size.y; */

            /*
            //Log.d(cTAG, "-> Grandezas gráficas:")
            //val displayMetrics = this.resources.displayMetrics
            //val intDyWidth     = displayMetrics.heightPixels
            //val intDyHeight    = displayMetrics.widthPixels
            //strLog = "   -Display: Largura: " + intDyWidth  + " pixels, Altura  : " +
            //                                                               intDyHeight + " pixels"
            //Log.d(cTAG, strLog)
            */

            //--- Margens do board
            intmargTopDp = resources.getDimension(R.dimen.MargemAcima).toInt()
            intMargleftdp = resources.getDimension(R.dimen.MargemEsquerda).toInt()
            intMargtoppx = toPixels2(this, intmargTopDp.toFloat())
            intMargleftpx = toPixels2(this, intMargleftdp.toFloat())

            //strLog = "   -Margens: Acima  :  " + intMargtoppx + " pixels, Esquerda:    " +
            //        intMargleftpx + " pixels"
            //Log.d(cTAG, strLog)

            //--- Imagem Sudoku board
            intImgwidth = bmpJogo!!.width
            intImgheight = bmpJogo!!.height   // bmpSudokuBoard!!.height
            //strLog = "   -Image  : Largura: " + intImgwidth + " pixels, Altura  :  " +
            //        intImgheight + " pixels"
            //Log.d(cTAG, strLog)

            //--- Células
            intCellwidth = intImgwidth / 9
            intCellheight = intImgheight / 9
            //strLog = "   -Célula : Largura:  " + intCellwidth + " pixels, Altura  :   " +
            //        intCellheight + " pixels"
            //Log.d(cTAG, strLog)

        } catch (exc: Exception) {
            val strErro = "Erro: " + exc.message
            Log.d(cTAG, strErro)
        }
    }

    //--- Converte um valor em dp para pixels
    // https://stackoverflow.com/questions/29664993/how-to-convert-dp-px-sp-among-each-other-especially-dp-and-sp/42108115#42108115
    private fun toPixels2(context: Context, dip: Float): Int {
        val r = context.resources
        val metrics = r.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics).toInt()
    }

    //----------------------------------------------------------------------------------------------
    // Funções para o jogo
    //----------------------------------------------------------------------------------------------
    //--- Determina a qual quadrado menor uma célula pertence
    fun determinaQm(linQM: Int, colQM: Int): Int {

        // Qm = (linha-mod(linha;3))+INT(col/3)  -> Excel/Algoritmos1
        return (linQM - linQM % 3 + colQM / 3)

    }

    //--- Calcula as linhas do QM para um Qm
    private fun calcLinsQM(quadMenor: Int): IntArray {

        // INT(EXT.TEXTO(F17;2;1)/3)*3
        val linInicQM = quadMenor / 3 * 3
        return intArrayOf(linInicQM, linInicQM + 1, linInicQM + 2)
    }

    //--- Calcula as colunas do QM para um Qm
    private fun calcColsQM(quadMenor: Int): IntArray {

        // EXT.TEXTO(F17;2;1)*3-INT(EXT.TEXTO(F17;2;1)/3)*9
        val colInicQM = quadMenor * 3 - quadMenor / 3 * 9
        return intArrayOf(colInicQM, colInicQM + 1, colInicQM + 2)
    }

    //--- Verifica se a coluna pertence ao quadMenor
    private fun colsQMcontem(idxColQM: Int, colsQM: IntArray): Boolean {
        var flagExiste = false
        for (idxAr in colsQM.indices) {
            if (colsQM[idxAr] == idxColQM) {
                flagExiste = true
                break
            }
        }
        return flagExiste
    }

    //--- Verifica se a linha pertence ao quadMenor
    private fun linsQMcontem(idxLinQM: Int, linhasQM: IntArray): Boolean {
        var flagExiste = false
        for (idxAr in linhasQM.indices) {
            if (linhasQM[idxAr] == idxLinQM) {
                flagExiste = true
                break
            }
        }
        return flagExiste
    }

    //--- listarQM
    private fun listarQM(quadMaior: Array<Array<Int>>) {

        var strDados: String
        for (linha in 0..8) {

            strLog = "linha $linha : "
            strDados = ""
            for (coluna in 0..8) {

                val strTmp = "${quadMaior[linha][coluna]}" + if (coluna < 8) ", " else ""
                strDados += strTmp
                strLog += strTmp

            }
            //Log.d(cTAG, strLog)

        }
    }

    //--- verifValidade de um número do Qm para inserção no QM
    fun verifValidade(quadMenor: Int, linQM: Int, colQM: Int, numero: Int): Boolean {

        var flagNumeroOk = true

        //--- Calcula as linhas desse quadrado menor no QM
        //-----------------------------------------
        val linhasQM = calcLinsQM(quadMenor)
        //-----------------------------------------
        //--- Calcula as colunas desse quadrado menor no QM
        //---------------------------------------
        val colsQM = calcColsQM(quadMenor)
        //---------------------------------------

        /*
		//--- Converte a linha do numero gerado do Qm para a do QM
		int linQM = linhasQM[linhaQm];
		//--- Converte a coluna do numero gerado do Qm para a do QM
		int colQM = colsQM[colunaQm];
		*/

        //-------------------------------------------------------
        // 1- verifica se número já existe no Qm da cel tocada
        //-------------------------------------------------------
        for (intLinha in linhasQM[0]..linhasQM[2]) {
            for (intColuna in colsQM[0]..colsQM[2]) {
                if (intLinha != intLinJogar || intColuna != intColJogar) {
                    if (arArIntNums[intLinha][intColuna] == numero) {
                        flagNumeroOk = false

                        //strLog  = "   - invalido Qm: Qm=$quadMenor; lin=$intLinha; col=$intColuna"
                        //strLog += " num=$numero"
                        //Log.d(cTAG, strLog)

                        break
                    }
                }
            }
        }
        //--- Se existe, retorna para gerar um novo numero
        if (!flagNumeroOk) return false

        //-----------------------------------------------
        // 2- verifica se número existe na LINHA do QM
        //-----------------------------------------------
        var numeroQM: Int
        for (idxColQM in 0..8) {

            //--- Se a coluna NÃO pertence ao quadMenor atual, verifica se o numero já existe nas
            //    outras linhas do maior, para a mesma coluna.
            if (!colsQMcontem(idxColQM, colsQM)) {
                numeroQM = arArIntNums[linQM][idxColQM]
                if (numero == numeroQM) {

                    flagNumeroOk = false

                    //strLog  = "   - invalido lin: linha=$linQM; col=$idxColQM; num=$numero"
                    //Log.d(cTAG, strLog)

                    break

                }
            }
        }

        //--- Se existe, retorna para gerar um novo numero
        if (!flagNumeroOk) return false

        //---------------------------------------------------------------------
        // 3- verifica se número existe na COLUNA do QM
        //---------------------------------------------------------------------
        for (idxLinQM in 0..8) {

            //--- Se a linha NÃO pertence ao quadMenor atual, verifica se o numero já existe nas
            //    outras linhas do quadMaior.
            if (!linsQMcontem(idxLinQM, linhasQM)) {
                numeroQM = arArIntNums[idxLinQM][colQM]
                if (numero == numeroQM) {
                    flagNumeroOk = false

                    //strLog  = "   - invalido col: linha=$idxLinQM; col=$colQM; num=$numero"
                    //Log.d(cTAG, strLog)

                    break

                }
            }
        }
        return flagNumeroOk
    }

    //--- iniciaJogo
    @RequiresApi(Build.VERSION_CODES.O)
    private fun iniciaJogo() {

        //*** Nesse ponto, arArIntNums (rascunho do jogo) e arArIntGab (gabarito) deverão estar Ok.

        intContaErro = intContaErroInic
        tvErros!!.text = "$intContaErro"

        //strNivelJogo    = strNivelJogoInic
        //strSubNivelJogo = strSubNivelJogoInic

        Log.d(cTAG, "-> Jogo:")
        //----------------------
        listarQM(arArIntNums)
        //----------------------
        Log.d(cTAG, "-> Gabarito:")
        //---------------------
        listarQM(arArIntGab)
        //---------------------

        //--- Desenha o SudokuBoard
        //----------------------------------
        desenhaSudokuBoard(true)
        //----------------------------------
        preencheJogo()
        //---------------
        iViewSudokuBoard!!.setImageBitmap(bmpJogo)

        //--- Apresenta o jogo preparado e prepara o array dos números disponíveis
        flagJoga = false

        //---------------
        preencheJogo()
        //---------------

//        iViewSudokuBoard!!.isEnabled = false
//        iViewNumsDisps!!.isEnabled = false

        //--- Apresenta os números disponíveis
        Log.d(cTAG, "-> Array qtidd de jogos disponível por número:")
        for (intIdxNum in 0..8) {

            val intQtiJogos = arIntNumsDisp[intIdxNum]
            Log.d(cTAG, String.format("   %s %d %d", "Num", intIdxNum + 1, intQtiJogos))

        }

        //------------------
        atualizaNumDisp()
        //------------------

        //--- Inicializa variáveis locais
        tvNivel!!.text = strNivelJogo
        tvSubNivel!!.text = strSubNivelJogo

        tvClues!!.text = utilsKt.quantZeros(arArIntNums).toString()

        //--- Verifica se fim de jogo
        var flagContJogo = false
        for (idxVetorNumDisp in 0..8) {

            if (arIntNumsDisp[idxVetorNumDisp] > 0) flagContJogo = true

        }
        //--- Se já foram utilizados todos os números disponíveis, pára o cronometro
        if (!flagContJogo) {

            Log.d(cTAG, "-> ${crono.text} - Fim")

            //------------
            fimDeJogo()
            //------------

        }
    }

    //--- PreencheJogo
    @RequiresApi(Build.VERSION_CODES.O)
    private fun preencheJogo() {

        arIntNumsDisp = Array(9) { 9 }     // intArrayOf(9, 9, 9, 9, 9, 9, 9, 9, 9)

        for (intLinha in 0..8) {

            for (intCol in 0..8) {

                //-------------------------------------------
                val intNum = arArIntNums[intLinha][intCol]
                //-------------------------------------------
                if (intNum > 0) {

                    val strTexto = intNum.toString()
                    pincelAzul.textSize = intTamTxt * scale // 50 // 200
                    //------------------------------------------------------
                    escreveCelula(intLinha, intCol, strTexto, pincelAzul)
                    //------------------------------------------------------
                    arIntNumsDisp[intNum - 1]--

                }
            }
        }
        iViewSudokuBoard!!.setImageBitmap(bmpJogo)
        //----------------------------------------------
        utilsKt.copiaBmpByBuffer(bmpJogo, bmpSalvaJogo)
        //----------------------------------------------

    }

    //--- salvaJogo
    @RequiresApi(Build.VERSION_CODES.O)
    private fun salvaJogo() {

        //----------------------------------------------------------------------
        // 1- Prepara o conteúdo
        //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        val strConteudo = preparaConteudo("modelo_arq_xml_sudoku1")
        //----------------------------------------------------------------------

        //----------------------------------------------------------------------
        // 2- Define um nome para o arquivo
        //----------------------------------------------------------------------
        if (strConteudo.isNotEmpty()) {

            //-------------------------------------------------------------------------------
            val arStrArqsNames = utils.listaExtMemArqDir("/sudoku/Jogos")
            //-------------------------------------------------------------------------------
            var intNumArq = 0
            if (arStrArqsNames.isNotEmpty()) {

                for (strArqName in arStrArqsNames) {

                    if (strArqName.contains("jogo_")) {

                        var intNumJogo: Int
                        try {
                            intNumJogo = strArqName.substring(
                                5,
                                strArqName.indexOf('.')
                            ).toInt()
                            if (intNumJogo > intNumArq) intNumArq = intNumJogo

                        } catch (exc: Exception) {
                        }
                    }

                }

            }
            intNumArq++
            val strArqJogo = "jogo_$intNumArq.xml"

            /* Nome com dataHora
            val strDataHora = utils.LeDataHora("yyMMddHHmmss")
            val strArqJogo = "jogo_$strDataHora.xml"
            */

            //----------------------------------------------------------------------
            // 3- Define o path
            //----------------------------------------------------------------------
            val strArqPath = "/sudoku/jogos"

            //------------------------------------------------------------------
            // Salva o arquivo
            //------------------------------------------------------------------
            //--------------------------------------------------------------------------------------
            val flagEscrita = utils.escExtMemTextFile(
                this, strArqPath, strArqJogo,
                strConteudo
            )
            //--------------------------------------------------------------------------------------

            strToast = "Escrita arquivo $strArqJogo "
            strToast += if (flagEscrita) "OK!" else "NÃO ok!"
            Toast.makeText(this, strToast, Toast.LENGTH_LONG).show()

            strLog = "-> Escrita arquivo storage/emulated/0/Download$strArqPath/$strArqJogo "
            strLog += if (flagEscrita) "OK!" else "NÃO ok!"
            Log.d(cTAG, strLog)

        }

    }

    //--- preparaConteudo
    //private var intIdxInic: Int = 0

    //--- preparaConteudo
    private fun preparaConteudo(strNomeArq: String): String {

        var strConteudo = ""

        //-- Lê o modelo para a formatação xml : modelo_arq_xml_sudoku1.txt
        val strNomeArqSemExt = strNomeArq    // SÓ a-z 0-9 _

        //--- Obtém lista de arquivos em resource / raw
        //-----------------------------------------------------
        val arStrNomeArqRaw: Array<String> = utils.ListRaw()
        //-----------------------------------------------------

        Log.d(cTAG, "-> Arquivos raw (listRaw):")

        var flagArqExiste = false
        for (idxFileName in arStrNomeArqRaw.indices) {

            val strFileNameDir = arStrNomeArqRaw[idxFileName]
            Log.d(cTAG, "$idxFileName: $strFileNameDir")

            flagArqExiste = (strFileNameDir == strNomeArqSemExt)
            if (flagArqExiste) break

        }

        //--- Leitura se arquivo existente
        var arStrLeitArqRaw = ArrayList<String>()
        if (flagArqExiste) {

            Log.d(cTAG, "-> Arquivo $strNomeArqSemExt:")
            //----------------------------------------------------------------------
            arStrLeitArqRaw = utils.LeituraFileRaw(this, strNomeArqSemExt)
            //----------------------------------------------------------------------
            for (idxDecl in 0 until arStrLeitArqRaw.size) {

                Log.d(cTAG, "   $idxDecl: ${arStrLeitArqRaw[idxDecl]}")

            }

        }

        //-- Converte o modelo de ArrayList para String
        if (strNomeArq == "modelo_arq_xml_sudoku1") {

            Log.d(cTAG, "-> modelo arq Sudoku xml")
            for (idxDecl in arStrLeitArqRaw.indices) {
                strModelo += arStrLeitArqRaw[idxDecl]
            }

            Log.d(cTAG, strModelo)

            //-- Preenche os campos
            var intTag = 0
            var strTag: String
            try {
                //- header / id
                intTag = 0
                intIdxFim = 0
                //-------------------------------------------------------------
                strConteudo = preencheConteudo("<id>", "3")
                //-------------------------------------------------------------

                //- header / nivel
                intTag = 1
                //---------------------------------------------------------------
                strConteudo += preencheConteudo("<nivel>", strNivelJogo)
                //---------------------------------------------------------------

                //- header / subnivel
                intTag = 2
                //---------------------------------------------------------------------
                strConteudo += preencheConteudo("<subnivel>", strSubNivelJogo)
                //---------------------------------------------------------------------

                //- body / linha0 até linha8 : jogo preparado
                intTag = 3
                for (idxLinha in 0 until 9) {

                    var strConteudoTmp = ""

                    for (idxCol in 0 until 9) {

                        strConteudoTmp += arArIntCopia[idxLinha][idxCol].toString()
                        if (idxCol < 8) strConteudoTmp += ", "

                    }

                    //--------------------------------------------------------------------
                    strConteudo += preencheConteudo("<linha$idxLinha>", strConteudoTmp)
                    //--------------------------------------------------------------------

                }

                //- jogos / id
                intTag = 4
                //----------------------------------------------------------------
                strConteudo += preencheConteudo("<id>", "1")
                //----------------------------------------------------------------

                //- opção de jogo - 02/01/2022 - versão 8.4
                intTag = 5
                //-------------------------------------------------------------------
                strConteudo += preencheConteudo("<opcaoJogo>", strOpcaoJogo)
                //-------------------------------------------------------------------

                //- jogos / dataHora
                intTag = 6
                val strDataHora = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //------------------------------------------------------
                    utils.LeDataHora("dd/MM/yyyy HH:mm:ss")
                    //------------------------------------------------------
                } else {
                    TODO("VERSION.SDK_INT < O")
                }

                intTag = 7
                //------------------------------------------------------------------
                strConteudo += preencheConteudo("<dataHora>", strDataHora)
                //------------------------------------------------------------------

                //- jogos / tempoJogo
                intTag = 8
                //--------------------------------------------------------------------------
                strConteudo += preencheConteudo("<tempoJogo>", crono.text.toString())
                //--------------------------------------------------------------------------

                //- jogos / erros
                intTag = 9
                //----------------------------------------------------------------------------
                strConteudo += preencheConteudo("<erros>", tvErros!!.text.toString())
                //----------------------------------------------------------------------------

                //- jogos / status
                intTag = 10
                //------------------------------------------------------------------------------------
                val strStatus =
                    if (utilsKt.quantZeros(arArIntNums) == 0) "finalizado" else "ativo"
                //------------------------------------------------------------------------------------
                strConteudo += preencheConteudo("<status>", strStatus)
                //-------------------------------------------------------------

                //- body2 / linha0 até linha8 : jogo no momento do salvamento se ainda ativo
                intTag = 11
                if (strStatus == "ativo") {

                    strConteudo += "</status></jogos><body2>"
                    var strConteudoTmp = ""
                    for (idxLinha in 0 until 9) {

                        strConteudoTmp += "<linha$idxLinha>"
                        for (idxCol in 0 until 9) {

                            //-----------------------------------------------------------
                            strConteudoTmp += arArIntNums[idxLinha][idxCol].toString()
                            //-----------------------------------------------------------
                            if (idxCol < 8) strConteudoTmp += ", "

                        }
                        strConteudoTmp += "</linha$idxLinha>"
                    }
                    strConteudo += strConteudoTmp
                    strConteudo += "</body2></presets>"

                } else {

                    //-- Finaliza a preparação do conteúdo
                    intTag = 12
                    strConteudo += strModelo.substring(intIdxFim, strModelo.length)

                }

                strTag = intTag.toString()
                Log.d(cTAG, "-> Tag: $strTag, Conteudo: $strConteudo")

            } catch (exc: Exception) {

                strTag = intTag.toString()
                utilsKt.mToast(this, "Erro $strTag: ${exc.message}")
                strConteudo = ""
            }

        } else {

            // TODO

        }

        return strConteudo

    }

    //--- preencheConteudo
    private fun preencheConteudo(strTag: String, strConteudoTag: String): String {

        var strConteudoPreenchido = ""

        val intIdxInic = intIdxFim
        intIdxFim = strModelo.indexOf(strTag, intIdxInic, false)
        intIdxFim += strTag.length

        strConteudoPreenchido += strModelo.substring(intIdxInic, intIdxFim)
        strConteudoPreenchido += strConteudoTag

        return strConteudoPreenchido

    }

    //----------------------------------------------------------------------------------------------
    //                                  Crono
    //----------------------------------------------------------------------------------------------
    //--- preparaCrono
    @RequiresApi(Build.VERSION_CODES.N)
    private fun preparaCrono(crono: Chronometer) {

        //--- Adiciona o cronometro ao layout
        crono.setTextColor(Color.BLUE)
        crono.setTextSize(TypedValue.COMPLEX_UNIT_IN, 0.15f)

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        //layoutParams.setMargins(30, 40, 120, 40)
        crono.layoutParams = layoutParams

        val linearLayout = findViewById<LinearLayout>(R.id.crono_layout)
        linearLayout?.addView(crono)

    }

    //--- acertaCrono
    private fun acertaCrono(strCronoInic: String) {

        Log.d(cTAG, "-> Acerta crono.")
        crono.stop()
        crono.text = strCronoInic

    }

    //--- ajustaCrono
    // https://developer.android.com/reference/android/os/SystemClock
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parteCrono(strCronoInic: String) {

        //--------------------------
        acertaCrono(strCronoInic)
        //--------------------------

        Log.d(cTAG, "-> Parte o crono.")

        crono.stop()

        crono.text = strCronoInic
        val intMin = strCronoInic.substring(0, 2).toLong()
        val intSec = strCronoInic.substring(3, 5).toLong()

        val timeLastStopped = ((intMin * 60 + intSec) * 1000)
        crono.base = SystemClock.elapsedRealtime() - timeLastStopped

        crono.start()

    }

    //----------------------------------------------------------------------------------------------
    //                             CounterDown Timer
    //----------------------------------------------------------------------------------------------
    //--- startTimer
    private fun startTimer(lgTimeOutMs: Long, intTimeTicksMs: Int) {

        ctimer = object : CountDownTimer(lgTimeOutMs, intTimeTicksMs.toLong()) {

            //--- Ticks
            override fun onTick(millisUntilFinished: Long) {

                if (crono.text == strFinalJogo) {

                    //------------------
                    verificaFimJogo()
                    //------------------

                }

            }

            //--- Finish
            override fun onFinish() {

                Log.d(cTAG, "-> TimeOut de ${(lgTimeOutMs / 1000L)} seg")

                //--------------
                timeOutJogo()
                //--------------

            }

        }

        //----------------
        ctimer.start()
        //----------------
    }

    //cancel timer
    private fun cancelTimer() {
        if (ctimer != null) ctimer.cancel()
    }

    //--- Finaliza jogo por tempo
    private fun verificaFimJogo() {

        if (crono.text == strFinalJogo) {

            utilsKt.mToast(this, "TimeOut de $strLimiteTempo !")

            //------------
            fimDeJogo()
            //------------

        }
    }

    //--- timeOutJogoPorTempo
    private fun timeOutJogo() {

        //--- Loop infinito para os tiques enqto estiver havendo jogo

        // "Mata" o objeto atual
        //----------------
        ctimer.cancel()
        //----------------

        // Instancia novo objeto CounterDown (TimeOUt 10' = 10 * 60 * 60 * 1000)
        //------------------------------
        startTimer(timeOut, timeTick)
        //------------------------------

    }

    //--- Final de jogo
    private fun fimDeJogo() {

        Log.d(cTAG, "-> ${crono.text} - Fim de jogo!")

        utilsKt.mToast(this, "Fim de jogo!")

        crono.stop()

        //-------------------------------------------------
        try {
            cancelTimer()
        } catch (exc: Exception) {
        }
        //-------------------------------------------------

        flagJoga = false

        btnInicia.text = strInicia
        btnInicia.isEnabled = false

    }

    //--- analisaCandidatos
    @RequiresApi(Build.VERSION_CODES.O)
    private fun analisaCandidatos() {

        //--- Instancializações e inicializações
        var intCell: Int
        arArIntCand = Array(81) { Array(9) { 0 } }

        // Commit9.0.184
        var salvaLinJogar = 0
        var salvaColJogar = 0

        //--- Inicia a análise
//        if (btnCand.text == "Candidatos") {

        //--- Salva o contexto
        arArSalvaJogo = utilsKt.copiaArArInt(arArIntNums)
        salvaLinJogar = intLinJogar
        salvaColJogar = intColJogar

        //--------------------------------------------------------
        //utilsKt.copiaBmpByBuffer(bmpJogo, bmpJogoSemCandidatos)
        //--------------------------------------------------------

//        }
        //--- Retorna ao Jogo
        // btnCand.text = "Volta Jogo"
        //       else {

        //--------------------------------------------------------
//            utilsKt.copiaBmpByBuffer(bmpJogoSemCandidatos, bmpJogo)
        //--------------------------------------------------------

//        }
        iViewSudokuBoard!!.setImageBitmap(bmpJogo)

        //--- Determina e apresenta os candidatos para as células vazias
        // Para todas as linhas de 0 a 8
        for (intLin in 0..8) {

            intLinJogar = intLin

            // Para todas as colunas de 0 a 8
            for (intCol in 0..8) {

                intColJogar = intCol

                intCell = intLin * 9 + intCol

                // Determina a que Qm a célula pertence
                //-----------------------------------------------
                val intQuadMenor = determinaQm(intLin, intCol)
                //-----------------------------------------------

                // Somente se célula vazia e número não repetido escreve o número candidato
                val intNumSKB = arArIntNums[intLin][intCol]
                if (intNumSKB == 0) {

                    // Para todos os números candidatos de 1 a 9 verifica se podem ser candidatos
                    //Log.d(cTAG, "-> Verifica candidatos:")
                    //strLog = "   linha = $intLin coluna = $intCol -> Qm = $intQuadMenor"
                    //Log.d(cTAG, strLog)

                    for (intNum in 1..9) {

                        //--------------------------------------
                        // OBS: breakpoint conf Qm e num Cond:
                        // (intQuadMenor == 3) && (intNum == 3)
                        //--------------------------------------

                        // Verifica se esse número ainda não existe no seu Qm e nem no seu QM
                        //--------------------------------------------------------------------
                        val flagNumValido = verifValidade(intQuadMenor, intLin, intCol, intNum)
                        //--------------------------------------------------------------------

                        if (flagNumValido) {

                            //Log.d(cTAG, "   - numCand = $intNum valido")

                            //------------------------------------------------
                            escCandidato(intLin * 9 + intCol, intNum)
                            //------------------------------------------------

                            //--- Armazena o candidato na matriz de candidatos x celula
                            arArIntCand[intCell][intNum - 1] = intNum

                        }
                    }
                }
            }
        }

        //--- Recupera as coordenadas da célula a jogar
        intLinJogar = salvaLinJogar
        intColJogar = salvaColJogar

        //--- Atualiza imageview
        //-------------------------------------------
        iViewSudokuBoard!!.setImageBitmap(bmpJogo)
        //-------------------------------------------

        //--- Lista a matriz candidatos por célula
        strLog = "-> \nMatriz candidatos por célula\n"
        for (intMatrizLin in 0..26) {

            for (intMatrizCol in 0..2) {

                val intIdx = (intMatrizLin * 3) + intMatrizCol
                strLog += String.format("%02d ", intIdx)

                for (intCand in 0..8) {

                    strLog += String.format(" %d", arArIntCand[intIdx][intCand])
                    strLog += if (intCand < 8) "," else "   "

                }

            }
            strLog += "\n"

        }
        Log.d(cTAG, strLog)

    }

    //--- retornaJogo
    private fun retornaJogo() {

        //--- Recupera o contexto
        //arArIntNums = utilsKt.copiaArArInt(arArSalvaJogo)
        utilsKt.copiaBmpByBuffer(bmpSalvaJogo, bmpJogo)
        iViewSudokuBoard!!.setImageBitmap(bmpJogo)

    }

    //--- escCandidato
    private fun escCandidato(intCell: Int, intCand: Int) {

        //--- Instancializações e inicializações
        val floCellWidth = intCellwidth.toFloat()
        val floCellHeight = intCellheight.toFloat()

        //val floCell = intCell.toFloat()
        //var floCand = intCand.toFloat()

        //--- Calcula coordenadas
        //  linha da célula
        val linhaCell = intCell / 9
        // coluna da célula
        val colCell = intCell % 9
        // coordenadas do canto superior da célula
        //----------------------------------------------
        val xSup = colCell.toFloat() * floCellWidth
        //----------------------------------------------
        val ySup = linhaCell.toFloat() * floCellHeight
        //----------------------------------------------
        //Log.d(cTAG, "   - xSup = $xSup ySup = $ySup")

        //--- Tamanhos parciais da célula
        val L9 = floCellWidth / 9F
        val L18 = floCellWidth / 18F
        val A3 = floCellHeight / 3F
        //val A6  = floCellHeight / 6F
        //Log.d(cTAG, "   - L9 = $L9 L18 = $L18 A3 = $A3 A6 = $A6")

        //--- Coordenadas para escrita do candidato
        // linha e coluna da celula (3x3)
        //  colunas   0   1   2
        //          +---+---+---+
        //- linha 0 | 1 | 2 | 3 |
        //          +---+---+---+
        //- linha 1 | 4 | 5 | 6 |
        //          +---+---+---+
        //- linha 2 | 7 | 8 | 9 |
        //          +---+---+---+

        val intLin = (intCand - 1) / 3
        val intCol = (intCand - 1) % 3

        //Log.d(cTAG, "   - coordCand linCell = $intLin colCell = $intCol")

        var posX = xSup + ((intCol * (3 * L9) + intCol * L9) + L18)
        //if (intCol > 0) posX -= (L9 + 4)
        val deltaX: Float = when (intCol) {

            1 -> -L9
            2 -> -(L9 + 4F)
            else -> 0F

        }
        posX += deltaX

        var posY = ySup + ((intLin + 1) * A3)
        if (intLin == 2) posY -= 4

        //--- Escrita do candidato na célula
        //Log.d(cTAG, "   - Cand: $intCand posX = $posX posY = $posY")

        // Tamanho do texto para candidatos
        val salvaTxtSize = pincelAzul.textSize

        pincelAzul.textSize = (intTamTxt.toFloat() / 2.5F) * scale
        //------------------------------------------------------------------
        canvasJogo!!.drawText(intCand.toString(), posX, posY, pincelAzul)
        //------------------------------------------------------------------
        pincelAzul.textSize = salvaTxtSize

        //--- Atualiza tabuleiro
        iViewSudokuBoard!!.setImageBitmap(bmpJogo)

    }

    //--- btnIniciaListener
    @RequiresApi(Build.VERSION_CODES.O)
    private fun btnIniciaListener() {

        //--------------------------------------------------------------
        // Legenda do botão: Inicia ou ReInicia
        //--------------------------------------------------------------
        if (btnInicia.text == strInicia || btnInicia.text == strReInicia) {

            strLog = if (btnInicia.text == strInicia) strInicia else strReInicia

            Log.d(cTAG, "-> ${crono.text} - $strLog")

            //-------------------------
            parteCrono(strCronoInic)
            //-------------------------

            //--- Se considerará limite de tempo, parte o timer CounterDown
            if ((btnInicia.text == strInicia) && (strLimiteTempo != "00:00")) {

                //--- Calcula a indicação do crono para o limite ajustado
                val intCrMin = strCronoInic.substring(0, 2).toInt()
                val intCrSeg = strCronoInic.substring(3, 5).toInt()

                val intAjMin = strLimiteTempo.substring(0, 2).toInt()
                val intAjSeg = strLimiteTempo.substring(3, 5).toInt()

                var intFinal = (intCrMin * 60 + intCrSeg) + (intAjMin * 60 + intAjSeg)
                // Limita o tempo a 59'59"
                if (intFinal > (59 * 60 + 59)) intFinal = (59 * 60 + 59)

                //--------------------------------------------------------------------------
                strFinalJogo = String.format("%02d:%02d", (intFinal / 60), (intFinal % 60))
                //--------------------------------------------------------------------------

            }

            //------------------------------
            startTimer(timeOut, timeTick)
            //------------------------------
            Log.d(cTAG, "-> Final crono: $strFinalJogo")

            btnInicia.text = strPause

        }

        //--------------------------------------------------------------------------------------
        // Legenda do botão: Pause
        //--------------------------------------------------------------------------------------
        else {

            Log.d(cTAG, "-> ${crono.text} - $strPause")

            //timeStopped = crono.base - SystemClock.elapsedRealtime()

            crono.stop()

            //-------------------------------------------------
            try {
                cancelTimer()
            } catch (exc: Exception) {
            }
            //-------------------------------------------------

            strCronoInic = crono.text.toString()

            btnInicia.text = strReInicia

        }
    }

    //--- selecionaCelSudokuBoard
    private fun selecionaCelSudokuBoard(x: Int, y: Int) {

        // Linha e coluna da célula tocada
        val intCol = x / intCellwidth
        val intLinha = y / intCellheight
        //-------------------------------------------
        val intNum = arArIntNums[intLinha][intCol]
        //-------------------------------------------
        //strLog = "-> Celula tocada: linha = " + intLinha + ", coluna = " + intCol +
        //        ", numero = " + intNum
        //Log.d(cTAG, strLog)

        //--- Se a célula tocada contiver um número, "pinta" todas as células que
        //    contiverem o mesmo número.
        if (intNum > 0) {
            flagJoga = false    // Não quer jogar; só quer analisar ...
            intLinJogar = 0
            intColJogar = 0
            //------------------------------------------------
            mostraNumsIguais(intNum, true)
            //------------------------------------------------
        }
        //--- Se não contiver um número, quer jogar
        else {

            flagJoga = true     // Vamos ao jogo!
            intLinJogar = intLinha
            intColJogar = intCol
            //------------------------------------------
            mostraCelAJogar(intLinJogar, intColJogar)
            //------------------------------------------

        }

        // Commit9.0.178
        //--- Se estiver mostrando os candidatos, atualiza-os.
        if (btnCand.text == "Volta Jogo") {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //--------------------
                analisaCandidatos()
                //--------------------

            }
        }
    }

    //--- selecionaCelNumDisp
    @RequiresApi(Build.VERSION_CODES.O)
    private fun selecionaCelNumDisp(intCol: Int) {

        var flagContJogo = false

        val intNum = intCol + 1
        val intQtidd = arIntNumsDisp[intNum - 1]
        //--- Verifica se ainda tem desse número para jogar e se esse número é válido.
        var flagNumValido: Boolean
        if (intQtidd > 0) {

            //--- Verifica se num válido
            // Determina a que Qm a célula pertence
            //----------------------------------------------------------
            val intQuadMenor = determinaQm(intLinJogar, intColJogar)
            //----------------------------------------------------------

            //Log.d(
            //    cTAG, "-> linhaJogar= " + intLinJogar + " colJogar= " +
            //            intColJogar + " Qm = " + intQuadMenor )

            // Verifica se esse número ainda não existe no seu Qm e nem no QM
            //------------------------------------------------------------------
            flagNumValido =
                verifValidade(intQuadMenor, intLinJogar, intColJogar, intNum)
            //------------------------------------------------------------------
            if (!flagNumValido) {

                //strLog  = "-> Número NÃO válido (linha, coluna ou quadro);"
                //strLog += " NÃO será incluído no Sudoku board."
                //Log.d(cTAG, strLog)

                strToast = "Número NÃO Ok (linha, coluna ou quadro)"
                //--------------------------------------
                utilsKt.mToast(this, strToast)
                //--------------------------------------

            }
            // Verifica se esse número, nessa célula, é o mesmo do gabarito
            else {

                /*
                strLog = "-> num= $intNum lin = $intLinJogar col = $intColJogar"
                Log.d(cTAG, strLog)
                val numGab = arArIntGab[intLinJogar][intColJogar]
                Log.d(cTAG, "   arArIntGab = $numGab")
                 */

                //--- Número diferente do num do gabarito
                if (intNum != arArIntGab[intLinJogar][intColJogar]) {

                    flagNumValido = false

                    //strLog = "-> Número NÃO válido (gab); NÃO será incluído" +
                    //         " no Sudoku board."
                    //Log.d(cTAG, strLog)

                    strToast = "Número NÃO Ok (gabarito)"
                    //--------------------------------------
                    utilsKt.mToast(this, strToast)
                    //--------------------------------------

                }
                //--- Número OK também qto ao gabarito -> número Válido
                else {

                    //Log.d(cTAG, "-> Número válido; será incluído no Sudoku board.")

                    //strToast = "Número Ok!"
                    //----------------------------------------------------------------
                    //Toast.makeText(this, strToast, Toast.LENGTH_LONG).show()
                    //----------------------------------------------------------------

                    //--- Atualiza o tabuleiro (bmpJogo)
                    //----------------------------------------------------
                    pintaCelula(intLinJogar, intColJogar, pincelBranco)
                    //----------------------------------------------------
                    escreveCelula(
                        intLinJogar,
                        intColJogar,
                        intNum.toString(),
                        pincelAzul
                    )

                    //--- Atualiza a base de dados
                    arArIntNums[intLinJogar][intColJogar] = intNum
                    arIntNumsDisp[intNum - 1]--

                    //--- Atualiza a qtidd disponível para esse número
                    //-------------------
                    atualizaNumDisp()
                    //-------------------

                    //-----------------------------------
                    desenhaSudokuBoard(true)
                    //-----------------------------------
                    preencheJogo()
                    //---------------

                    //--- Salva o Jogo
                    utilsKt.copiaBmpByBuffer(bmpJogo, bmpSalvaJogo)

                    // Commit9.0.185
                    //--- Atualiza o Jogo SEM candidatos (bmpJogoSemCandidatos)
                    if (btnCand.text == "Volta Jogo") {

                        // utilsKt.copiaBmpByBuffer(bmpJogoSemCandidatos, bmpJogo)
                        /*
                        escreveCelula(
                            intLinJogar,
                            intColJogar,
                            intNum.toString(),
                            pincelAzul
                        )
                        */
                        //utilsKt.copiaBmpByBuffer(bmpJogo, bmpJogoSemCandidatos)

                        //--- Retorna o Jogo presente no tabuleiro
                        //utilsKt.copiaBmpByBuffer(bmpSalvaJogo, bmpJogo)

                        //--------------------
                        analisaCandidatos()
                        //--------------------


                    }

                    //--- Destaca os números iguais a esse já jogados
                    //------------------------------------------------
                    mostraNumsIguais(intNum, false)
                    //------------------------------------------------

                    flagJoga = false

                }
            }

            if (!flagNumValido) {

                //--- Se não tem limite de erros, apenas computa-o
                //--- Se tem limite de erros, ao incrementá-lo informa qdo = lim
                tvErros!!.text = "${++intContaErro}"
                if (intContaErro == intLimiteErros) {

                    var strMsg = "Você atingiu o limite de erros."
                    strMsg += "\nTenta até outro erro?"

                    val builder =
                        androidx.appcompat.app.AlertDialog.Builder(this)
                    builder.setTitle(strMsg)
                        .setPositiveButton("Sim") { _, _ ->

                            Log.d(cTAG, "-> Tenta mais 1 erro.")
                            intLimiteErros++
                            MainActivity.strTitleErros = "$intLimiteErros"

                            strLog = "Novo limite: $intLimiteErros erros"
                            utilsKt.mToast(this, strLog)

                            //MainActivity.intLimiteErrosAtual = intLimiteErros

                        }
                        .setNegativeButton("Não") { _, _ ->

                            Log.d(cTAG, "-> Encerra o jogo.")

                            Log.d(cTAG, "-> ${crono.text} - Fim")

                            //------------
                            fimDeJogo()
                            //------------

                        }

                    // Apresenta o AlertDialog
                    val alert = builder.create()
                    alert.show()

                    // Colore as legendas dos botões de opção
                    val pbuttonPos = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                    pbuttonPos.setTextColor(Color.BLUE)

                    val pbuttonNeg = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                    pbuttonNeg.setTextColor(Color.RED)

                }
            }
        }

        //--- Verifica se fim de jogo (todas as qtidds foram zeradas
        for (idxVetorNumDisp in 0..8) {

            if (arIntNumsDisp[idxVetorNumDisp] > 0) flagContJogo = true

        }

        //--- Se já foram utilizados todos os números disponíveis, pára o cronometro
        if (!flagContJogo) {

            Log.d(cTAG, "-> ${crono.text} - Fim")

            //------------
            fimDeJogo()
            //------------

        }

        // Commit9.0.178
        //--- Ainda não é fim de jogo e se estiver mostrando os candidatos,
        //    atualiza-os.
        if (btnCand.text == "Volta Jogo") {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //--------------------
                analisaCandidatos()
                //--------------------

            }
        }
    }

}
