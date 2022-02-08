package br.com.jhconsultores.sudoku.ui

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log

import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import android.view.View.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import br.com.jhconsultores.sudoku.R
import br.com.jhconsultores.sudoku.jogo.SudokuGameGenerator
import br.com.jhconsultores.utils.*
import java.lang.Exception
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.*
import android.net.Uri

import android.os.Build
import android.provider.Settings
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.core.view.MenuCompat
import br.com.jhconsultores.sudoku.BuildConfig
import kotlin.math.sqrt

const val ALL_FILES_ACCESS_PERMISSION = 4

@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {

    //----------------------------------------------------------------------------------------------
    //                         Instancializações e inicializações
    //----------------------------------------------------------------------------------------------
    private var strLog   = ""
    private var strToast = ""

    companion object {
        const val cTAG   = "Sudoku"
        const val strApp = "Sudoku_#9.0.185"

        var flagScopedStorage  = false

        var flagJogoGeradoOk   = false
        var flagJogoEditadoOk  = false
        var flagJogoAdaptadoOk = false

        // Opções de jogo: "JogoGerado", "JogoAdaptado", "JogoPresetado", "JogoEditado"
        var strOpcaoJogo = "JogoGerado"

        val arStrNivelJogo   = arrayOf("Fácil", "Médio", "Difícil", "Muito difícil")
        const val idxFACIL   = 0
        const val idxMEDIO   = 1
        const val idxDIFICIL = 2
        const val idxMUITO_DIFICIL = 3

        var fatorLargura: Double = 0.0
        var fatorAltura : Double = 0.0

        //--- Algoritmos do jogo
        const val ALGORITMO_JH    = "AlgoritmoJH"
        const val ALGORITMO_SCOTT = "AlgoritmoScott"

        //--- Params
        // Erros
        const val TITLE_ERROS = "Limite Erros: "
        var strTitleErros  = ""
        var intLimiteErros = -1          // Default; significa "sem limite"
        var intLimiteErrosAtual = -1

        // Tempo do Jogo
        const val TITLE_TEMPO   = "Limite Tempo: "
        var strTitleTempo       = ""
        var strLimiteTempo      = "00:00"    // Default; significa "sem limite"
        var strLimiteTempoAtual = "00:00"
        // Mostrar números iguais
        var flagMostraNumIguais      = true
        var flagMostraNumIguaisAtual = true

    }

    //--- Objetos gráficos
    private lateinit var ivSudokuBoardMain: ImageView
    private lateinit var ivNumDisp: ImageView

    private var bmpMyImageInic: Bitmap? = null
    private var bmpMyImageBack: Bitmap? = null
    private var bmpMyImage    : Bitmap? = null

    private var bmpNumDisp: Bitmap? = null

    private var intCellwidth  = 0
    private var intCellheight = 0

    private var pincelVerde   = Paint()
    private var pincelBranco  = Paint()
    private var pincelPreto   = Paint()
    private var pincelAzul    = Paint()
    private var pincelLaranja = Paint()

    private var intTamTxt = 25
    private var scale     = 0f

    private var canvasMyImage: Canvas? = null
    private var canvasNumDisp: Canvas? = null

    //--- Textos
    private lateinit var tvContaNums : TextView
    private lateinit var tvContaClues: TextView

    //--- Botões principais
    private lateinit var btnGeraJogo  : Button
    private lateinit var btnAdaptaJogo: Button
    private lateinit var btnJogaJogo  : Button

    //--- Radio Buttons
    private lateinit var groupRBnivel: RadioGroup
    private lateinit var rbFacil     : RadioButton
    private lateinit var rbMedio     : RadioButton
    private lateinit var rbDificil   : RadioButton
    private lateinit var rbMuitoDificil: RadioButton

    private lateinit var groupRBadapta: RadioGroup
    private lateinit var rbPreset: RadioButton
    private lateinit var rbEdicao: RadioButton

    private lateinit var layout         : RelativeLayout
    private lateinit var progressBar    : ProgressBar

    private lateinit var edtViewSubNivel: EditText

    //--- Objetos para o jogo
    private var quadMaior = Array(9) { Array(9) { 0 } }

    private var strNivelJogo = "Fácil"
    private var nivelJogo    = 0
    private var subNivelJogo = 0
    private var nivelTotalJogo = 0

    // Núm     0   22               31        41          51       61     81
    // clues  81   59               50        40          30       20      0
    //        |----|----------------|---------|-----------|--------|-------|
    //        |xxxx|  MUITO DIFÍCIL | DIFÍCIL |   MÉDIO   |  FÁCIL |xxxxxxx|
    //        |----|----------------|---------|-----------|--------|-------|

    private val FACIL   = 20
    private val MEDIO   = 30
    private val DIFICIL = 40
    private val MUITO_DIFICIL = 50

    private var arArIntNums = Array(9) { Array(9) { 0 } }
    private var arIntQtiNumDisp = Array(9) { 9 }
    private val arIntNumsDisp = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

    private lateinit var txtDadosJogo: TextView

    //--- Controle de jogadas
    private var intColJogar = 0
    private var intLinJogar = 0

    private var flagBoardSel = false
    private var versAndroid  = ""

    //--- Parâmetros do jogo
    //private lateinit var layoutAjustes : Layout
    private lateinit var edtLimErros: EditText
    private lateinit var edtLimTempo: EditText

    //private lateinit var ctimer: CountDownTimer
    //private var timeOut  = (10 * 60 * 60 * 1000).toLong()   // 10'
    //private var timeTick = 1000                             //  1"

    private var flagPermissoesOk = false

    //--- Arquivos jogos
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar
    private lateinit var previewRequest: ActivityResultLauncher<Intent>

    //--- Classes externas
    private var sgg = SudokuGameGenerator()
    private var jogarJogo = JogarActivity()
    private val utils   = Utils()
    private val utilsKt = UtilsKt()
    private val timeVal = TimeValidator()

    //----------------------------------------------------------------------------------------------
    // Eventos e listeners da MainActivity
    //----------------------------------------------------------------------------------------------
    //--- onCreate
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //----------------------------------------------------------------------
        //                              Device
        //----------------------------------------------------------------------
        //--- Smartphone
        strToast = "Smartphone: ${Build.MANUFACTURER} - ${Build.MODEL}"
        strLog   = strToast + "  Build: ${Build.DEVICE}"
        Log.d(cTAG, strLog)

        //--- Calcula o fator a ser usado para as diferenças de tamanho dos screens
        // desenv Smartphone JH: Samsung SM-G570M
        val larguraDesenv = 2.44  // inches
        val alturaDesenv  = 4.33  // inches

        val metrics = resources.displayMetrics
        val yInches = metrics.heightPixels.toDouble() / metrics.ydpi.toDouble()
        val xInches = metrics.widthPixels.toDouble() / metrics.xdpi.toDouble()

        Log.d(cTAG, "-> Dimensões screen:")
        strLog =
            String.format("%s %.2f%s", "   - largura  : ", metrics.widthPixels.toDouble(), " px")
        strLog += String.format(" (%.2f\")", xInches)
        Log.d(cTAG, strLog)
        strLog =
            String.format("%s %.2f%s", "   - altura   : ", metrics.heightPixels.toDouble(), " px")
        strLog += String.format(" (%.2f\")", yInches)
        Log.d(cTAG, strLog)
        val diagonalInches = sqrt(xInches * xInches + yInches * yInches)
        Log.d(cTAG, String.format("%s %.2f%s", "   - Diagonal : ", diagonalInches, "\""))

        fatorLargura = xInches / larguraDesenv
        fatorAltura = yInches / alturaDesenv

        Log.d(cTAG, String.format("%s %.2f", "   - fatorlargura : ", fatorLargura))
        Log.d(cTAG, String.format("%s %.2f", "   - fatorAltura  : ", fatorAltura))

        //--- Versão do Android e versão da API
        versAndroid =
            Build.VERSION.RELEASE    //.BASE_OS  // samsung/on5xelteub/on5xelte:8.0.0/R16NW/G570MUBU4CSB1:user/release-keys
        var strSO = ""
        try {
            strSO = versAndroid
        } catch (exc: Exception) {

            //---------------------------------------------------------------
            utilsKt.mToast(this, "-> Erro01: ${exc.message}")
            //---------------------------------------------------------------

        }

        utils.flagSO_A11 = (strSO == "11")

        Log.d(cTAG, "-> BaseOS: $strSO SDK_INT: API${Build.VERSION.SDK_INT}")
        strToast += "\nAndroid $strSO  API ${Build.VERSION.SDK_INT}"
        //--------------------------------------
        utilsKt.mToast(this, strToast)
        //--------------------------------------

        //----------------------------------------------------------------------
        // Implementa o actionBar
        //----------------------------------------------------------------------
        toolBar = findViewById(R.id.maintoolbar)
        toolBar.title = "$strApp - main"
        setSupportActionBar(toolBar)

        //----------------------------------------------------------------------
        // Instancializações e inicializações
        //----------------------------------------------------------------------
        tvContaNums  = findViewById(R.id.ContaNums)
        tvContaClues = findViewById(R.id.ContaClues)

        btnGeraJogo   = findViewById(R.id.btn_GerarJogo)
        btnAdaptaJogo = findViewById(R.id.btn_AdaptarJogo)
        btnJogaJogo   = findViewById(R.id.btn_JogarJogo)

        groupRBnivel = findViewById(R.id.radioGrpNivel)
        rbFacil   = findViewById(R.id.nivelFacil)
        rbMedio   = findViewById(R.id.nivelMédio)
        rbDificil = findViewById(R.id.nivelDifícil)
        rbMuitoDificil = findViewById(R.id.nivelMuitoDifícil)
        //-----------------------------
        prepRBniveis(true)
        //-----------------------------
        edtViewSubNivel = findViewById(R.id.edtViewSubNivel)

        groupRBadapta = findViewById(R.id.radioGrpAdapta)
        rbPreset = findViewById(R.id.preset)
        rbEdicao = findViewById(R.id.edicao)
        txtDadosJogo = findViewById(R.id.txtJogos)

        groupRBadapta.visibility = INVISIBLE

        tvContaNums.text = "0"
        tvContaClues.text = getString(R.string.valor81)

        //--- Objetos gráficos
        //--------------------
        inicializaObjGraf()
        //--------------------

        //----------------------------------------------------------------------
        // Implementa o Progress Bar
        //----------------------------------------------------------------------
        progressBar = ProgressBar(this)

        //setting height and width of progressBar
        progressBar.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        //accessing our relative layout where the progressBar will add up
        layout = findViewById(R.id.relLayoutProgBar)
        // Add ProgressBar to our layout
        layout.addView(progressBar)

        //progressBar.show()
        progressBar.visibility = VISIBLE

        //------------------------------------------------
        // Listener para mudança do subnivel (editView)
        //------------------------------------------------
        //https://www.tutorialkart.com/kotlin-android/android-edittext-on-text-change/
        edtViewSubNivel.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isNotEmpty())
                //-----------------------------
                    verifMudancaNivel(nivelJogo)
                //-----------------------------

            }

        })

        //------------------------------------------------------------------------------------------
        // Listener para os eventos onTouch dos ImageViews
        // (só utilizados na edição)
        //------------------------------------------------------------------------------------------
        ivSudokuBoardMain.setOnTouchListener { _, event -> //--- Coordenadas tocadas

            if (groupRBadapta.isVisible && rbEdicao.isChecked) {

                Log.d(cTAG, "-> SudokuBoard:")

                val x = event.x.toInt()
                val y = event.y.toInt()
                //Log.d(cTAG, "touched x: $x  y: $y")

                //-------------------------
                editaIVSudokuBoard(x, y)
                //-------------------------

            }

            false

        }

        // https://www.geeksforgeeks.org/add-ontouchlistener-to-imageview-to-perform-speech-to-text-in-android/
        ivNumDisp.setOnTouchListener { _, motionEvent ->

            Log.d(cTAG, "-> ivNumDisp:")

            when (motionEvent.action) {

                MotionEvent.ACTION_UP -> {
                    Log.d(cTAG, "   - ACTION_UP")
                }

                MotionEvent.ACTION_DOWN -> {
                    Log.d(cTAG, "   - ACTION_Down")

                    //Log.d(cTAG, "   - ACTION_DOWN")
                    val x = motionEvent.x.toInt()
                    //Log.d(cTAG, "   touched x: $x")

                    //-------------------
                    editaIVNumDisp(x)
                    //-------------------

                }

            }

            false

        }

        //------------------------------------------------------------------------------------------
        // Listener para os eventos dos buttons: declarados em res/layout/activity_main.xml
        //------------------------------------------------------------------------------------------
        // fun btnGeraJogoClick  (view: View?) {}
        // fun btnAdaptaJogoClick(view: View?) {}
        // fun btnJogaJogoClick  (view: View?) {}

        //-------------------------------------------------------------------------------------
        // Prepara retorno da StartActivityForResult() Kotlin; utilizada na DELEÇÃO de Jogos
        //-------------------------------------------------------------------------------------
        Log.d(cTAG, "-> Registra activity for result")

        //------------------------------------------------------------------------------------------
        previewRequest = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
        //------------------------------------------------------------------------------------------

            Log.d(cTAG, "-> Retorna da activity for result")

            if (result.resultCode == Activity.RESULT_OK) {

                //  you will get result here in result.data
                // val list = result.data

                // do whatever with the data in the callback
                val strStatus = result.data!!.getStringExtra("Status")
                Log.d(cTAG, "-> activity results OK! Status: $strStatus")

                //--- Se deletou jogo retorna à adaptação de jogo
                if (strStatus == "Deletar Jogo") {

                    //--- Prepara a Intent para chamar AdaptarActivity
                    val intent    = Intent(this, AdaptarActivity::class.java)
                    intent.action = "InstanciaRVJogosSalvos"
                    //------------------------------
                    previewRequest.launch(intent)
                    //------------------------------

                }

            } else {

                Log.d(cTAG, "-> activity results NÃO OK!")

            }
        }

        //-----------------------------------------------------------------------------------
        // Verifica se permitidos os acessos aos recursos do Android (AndroidManifest.xml)
        //-----------------------------------------------------------------------------------
        //---------------------------
        verifPermissoesAcessoAPI()
        //---------------------------

    }

    //--- finalizaInicialização
    // Ativada por verifPermissoesAcessoAPI()
    private fun finalizaInicializacao () {

        var flagLeitSetUpOK = false

        // Acessos Granted
        if (flagPermissoesOk) {

            //-------------------------------
            flagLeitSetUpOK = leArqSetUp()
            //-------------------------------

            //--- Leitura Ok, usa os valores lidos
            if (flagLeitSetUpOK) {

                intLimiteErros      = intLimiteErrosAtual
                strLimiteTempo      = strLimiteTempoAtual
                flagMostraNumIguais = flagMostraNumIguaisAtual

            }
            //--- Leitura NÃO ok, usa valores default
            else {

                strToast  = "Leitura set up não Ok! Usa default"
                utilsKt.mToast(this, strToast)
                Log.d(cTAG, "-> $strToast")

                intLimiteErrosAtual = -1
                intLimiteErros      = intLimiteErrosAtual

                strLimiteTempoAtual = "00:00"
                strLimiteTempo      = strLimiteTempoAtual

                flagMostraNumIguaisAtual = true
                flagMostraNumIguais      = flagMostraNumIguaisAtual

            }

        }
        // Acessos não-Granted
        else {

            strToast  = " Permissões não Ok! Usa default"
            utilsKt.mToast(this, strToast)
            Log.d(cTAG, "-> $strToast")

            /*
            //------------------------------------------------------------------
            // Salva o arquivo
            //------------------------------------------------------------------
            val strArqPath  = "/sudoku/setup"
            val strArqSetUp = "sudokusetup.xml"
            var strConteudo = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            strConteudo    += "<setup><header><id>modeloArqXmlSudokuSetUp.txt</id></header>"
            strConteudo    += "<body><limite_erros>-1</limite_erros>"
            strConteudo    += "<limite_tempo>00:00</limite_tempo>"
            strConteudo    += "<mostra_nums_iguais>true</mostra_nums_iguais></body></setup>"

            //--------------------------------------------------------------------------------------
            val flagEscrita = utils.escExtMemTextFile(
                this, strArqPath, strArqSetUp,
                strConteudo
            )
            //--------------------------------------------------------------------------------------

            strToast = "Escrita arquivo $strArqSetUp "
            strToast += if (flagEscrita) "OK!" else "NÃO ok!"
            utilsKt.mToast(this, strToast)

            strLog = "-> Escrita arquivo storage/emulated/0/Download$strArqPath/$strArqSetUp "
            strLog += if (flagEscrita) "OK!" else "NÃO ok!"
            Log.d(cTAG, strLog)
             */

            intLimiteErrosAtual = -1
            intLimiteErros      = intLimiteErrosAtual

            strLimiteTempoAtual = "00:00"
            strLimiteTempo      = strLimiteTempoAtual

            flagMostraNumIguaisAtual = true
            flagMostraNumIguais      = flagMostraNumIguaisAtual

        }

    }

    //---------------------------------------------------------------------
    //                       Action Bar Menu
    //---------------------------------------------------------------------
    private lateinit var myMenuItem: MenuItem
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        try {

            menuInflater.inflate(R.menu.menu_sudoku, menu)

            MenuCompat.setGroupDividerEnabled(menu, true)

            // https://stackoverflow.com/questions/27627659/android-actionbar-items-as-three-dots/28238747
            // Find the menuItem to add your SubMenu
            myMenuItem = menu.findItem(R.id.sudoku)

            // Inflating the sub_menu menu this way, will add its menu items
            // to the empty SubMenu you created in the xml
            menuInflater.inflate(R.menu.menu_main_sub, myMenuItem.subMenu)

        } catch (exc: Exception) {
            Log.d(cTAG, "-> Erro02: ${exc.message}")
        }

        return true

    }

    //----------------------------------------------------------------------------------
    // Listener para seleção de opções no menu pop-up do ActionBar (three dots menu )
    //----------------------------------------------------------------------------------
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        //--- Tapping no item 'Ajustar quantidade de erros' do menu do actionBar
        R.id.action_ajustarQtiddErros -> {

            Log.d(cTAG, "-> Tap em actionBar / Ajusta limite contagem de erros")

            //----------------------------
            implDialogViewAlertDialog()
            //----------------------------

            true

        }

        //--- Tapping no item 'Ajustar tempo de jogo' do menu do actionBar
        R.id.action_ajustarTempoDeJogo -> {

            Log.d(cTAG, "-> Tap em actionBar / Ajusta tempo de jogo")

            //----------------------------
            implDialogViewAlertDialog()
            //----------------------------

            true

        }

        //--- Tapping no item 'Deletar selecionados' do menu do actionBar
        R.id.action_mostrar_numeros_iguais -> {

            if (flagMostraNumIguais) {

                strLog = "Não mostrar números iguais"
                flagMostraNumIguais = false

            } else {

                strLog = "Mostrar números iguais"
                flagMostraNumIguais = true

            }
            Log.d(cTAG, "-> Tap em actionBar / $strLog")

            //---------------------------------------------------------------------
            atualizaSubMenu()  //intLimiteErros, strLimiteTempo, flagMostraNumIguais)
            //---------------------------------------------------------------------

            true

        }

        //--- Tapping no item do menu do actionBar 'Sair'
        R.id.action_exit -> {

            // User chose the "Favorite" action, mark the current item  as a favorite...
            Log.d(cTAG, "-> Tap em actionBar / Desligar App")
            androidx.appcompat.app.AlertDialog.Builder(this)

                .setTitle("Sudoku - Sair do Jogo")
                .setMessage("Finaliza o App?")

                .setPositiveButton("Sim") { _, _ ->

                    Log.d(cTAG, "-> Confirma que FIM!")

                    //---------
                    finish()
                    //---------

                }
                .setNegativeButton("Não") { _, _ -> Log.d(cTAG, "-> Não finaliza App.") }

                .show()

            true

        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)

        }

    }

    @SuppressLint("RestrictedApi")
    override fun onResume() {

        super.onResume()

        if (rbEdicao.isChecked) {
            txtDadosJogo.text = ""
        }

        //--- Ativa o progress bar
        //progressBar.show()
        //progressBar.visibility = VISIBLE
        layout.visibility = VISIBLE

        //--- Aguarda o teclado ser mostrado para poder escondê-lo
        val waitTime = 3000L  // milisegundos
        Handler(Looper.getMainLooper()).postDelayed(
            {

                //--- Desativa o progress bar
                //progressBar.dismiss()
                //progressBar.visibility = INVISIBLE
                layout.visibility = INVISIBLE

                //--- Esconde o teclado
                //----------------------------------
                utils.EscondeTeclado(this)
                //----------------------------------

                //------------------------
                atualizaTitlesSubMenus()
                //------------------------

                //------------------
                atualizaSubMenu()
                //------------------
            },
            waitTime
        )  // value in milliseconds

        groupRBadapta.visibility = INVISIBLE
        //-----------------------------
        prepRBniveis(true)
        //-----------------------------
        edtViewSubNivel.isEnabled = true

        //-----------------------------
        visibilidadeViews(INVISIBLE)
        //-----------------------------

    }

    //----------------------------------------------------------------------------------------------
    // Funções para o atendimento ao tapping nos botões (declaradas no xml)
    //----------------------------------------------------------------------------------------------
    //--- Evento tapping no botão de geração de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnGeraJogoClick(view: View?) {


        strLog = "-> Tap no btnGeraJogo"
        Log.d(cTAG, strLog)

        strOpcaoJogo = "JogoGerado"
        flagJogoGeradoOk = false

        //--- Ativa o progress bar
        //progressBar.show()
        //progressBar.visibility = VISIBLE
        layout.visibility = VISIBLE

        txtDadosJogo.text = ""   // String.format("%s", "Aguarde ...")
        txtDadosJogo.bringToFront()

        //--- Continua a adaptação após um tempo para atualização da UI (progress bar e txtDadosJogo)
        val waitTime = 100L  // milisegundos
        Handler(Looper.getMainLooper()).postDelayed(
            {

                //-----------------------------
                visibilidadeViews(INVISIBLE)
                //-----------------------------

                rbPreset.isChecked = true

                sgg.txtDados = ""

                //--------------------------
                prepRBniveis(true)
                //--------------------------
                edtViewSubNivel.isEnabled = true

                groupRBadapta.visibility  = INVISIBLE

                txtDadosJogo.text = ""
                sgg.txtDados = ""

                //--- Gera um novo jogo
                nivelJogo = when {

                    rbFacil.isChecked -> {
                        strNivelJogo = arStrNivelJogo[idxFACIL]
                        FACIL
                    }

                    rbMedio.isChecked -> {
                        strNivelJogo = arStrNivelJogo[idxMEDIO]
                        MEDIO
                    }
                    rbDificil.isChecked -> {
                        strNivelJogo = arStrNivelJogo[idxDIFICIL]
                        DIFICIL
                    }

                    rbMuitoDificil.isChecked -> {
                        strNivelJogo = arStrNivelJogo[idxMUITO_DIFICIL]
                        MUITO_DIFICIL
                    }

                    else -> {
                        strNivelJogo = "Muito fácil"
                        0
                    }

                }
                strLog = "   - Nível: $strNivelJogo ($nivelJogo) Subnível: ${edtViewSubNivel.text}"
                Log.d(cTAG, strLog)

                if (edtViewSubNivel.text.toString().isNotEmpty()) {

                    subNivelJogo = edtViewSubNivel.text.toString().toInt()
                    nivelTotalJogo = nivelJogo + subNivelJogo

                    //=======================================================
                    quadMaior = sgg.geraJogo(nivelTotalJogo, ALGORITMO_JH)
                    //=======================================================

                    //==========================================================
                    //quadMaior = sgg.geraJogo(nivelTotalJogo, ALGORITMO_SCOTT)
                    //==========================================================

                    //-------------------------------
                    preencheSudokuBoard(quadMaior)
                    //-------------------------------

                    txtDadosJogo.text = ""

                    //--- Desativa o progress bar
                    //progressBar.dismiss()
                    //progressBar.visibility = INVISIBLE
                    layout.visibility = INVISIBLE

                    flagJogoGeradoOk = true

                    // **** O array preparado (quadMaior) será enviado pelo listener do botão JogaJogo ****

                } else {

                    txtDadosJogo.text = ""

                    //--- Desativa o progress bar
                    //progressBar.dismiss()
                    //progressBar.visibility = INVISIBLE
                    layout.visibility = INVISIBLE

                    strLog = "Não é possível gerar o jogo sem subnivel!"
                    //------------------------------------------
                    utilsKt.mToast(this, strLog)
                    //------------------------------------------
                    Log.d(cTAG, "-> $strLog")

                }

            },
            waitTime      // value in milliseconds
        )

    }

    //--- Evento tapping no botão de adaptação de jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnAdaptaJogoClick(view: View?) {

        strOpcaoJogo = "JogoAdaptado"

        strLog = "-> Tap no btnAdaptaJogo"
        Log.d(cTAG, strLog)

        txtDadosJogo.text = ""
        sgg.txtDados = ""

        strLog = "   - rbAdapta: "
        strLog += if (rbPreset.isChecked) "Preset" else "Edição"
        Log.d(cTAG, strLog)

        //---------------------------
        visibilidadeViews(VISIBLE)
        //---------------------------
        groupRBadapta.visibility = VISIBLE

        //------------------------------
        prepRBniveis(false)
        //------------------------------
        edtViewSubNivel.isEnabled = false

        //--- Continua a adaptação após um tempo para atualização da UI (progress bar e txtDadosJogo)
        try {
            val waitTime = 100L  // milisegundos
            Handler(Looper.getMainLooper()).postDelayed(
                {

                    androidx.appcompat.app.AlertDialog.Builder(this)

                        .setTitle("Sudoku - Adaptar Jogo")
                        .setMessage("Carrega ou Edita jogo?")

                        .setPositiveButton("Carrega") { _, _ ->

                            Log.d(cTAG, "-> \"Carrega\" was pressed")

                            txtDadosJogo.bringToFront()

                            //---------------
                            adaptaPreset()
                            //---------------

                        }

                        .setNegativeButton("Edita") { _, _ ->

                            Log.d(cTAG, "-> \"Edita\" was pressed")
                            strOpcaoJogo = "JogoEditado"

                            txtDadosJogo.visibility = INVISIBLE

                            //---------------
                            edicaoPreset()
                            //---------------

                        }

                        .setNeutralButton("Cancela") { _, _ ->

                            Log.d(cTAG, "-> \"Cancela\" was pressed")

                        }
                        .show()

                },
                waitTime
            ) // value in milliseconds
        } catch (exc: Exception) {

            Log.d(cTAG, "-> adapta Erro: ${exc.message}")

        }

    }

    //--- Evento tapping no botão para jogar o jogo
    @Suppress("UNUSED_PARAMETER")
    fun btnJogaJogoClick(view: View?) {

        strLog = "-> Tap no btnJogaJogo"
        Log.d(cTAG, strLog)

        txtDadosJogo.bringToFront()

        // <DEBUG>
        /* Teste 1
        quadMaior [0] = arrayOf (8, 0, 0, 0, 1, 0, 0, 0, 9 )
        quadMaior [1] = arrayOf (0, 9, 0, 0, 2, 0, 0, 8, 0 )
        quadMaior [2] = arrayOf (0, 0, 6, 0, 3, 0, 7, 0, 0 )
        quadMaior [3] = arrayOf (0, 0, 0, 7, 9, 6, 0, 0, 0 )
        quadMaior [4] = arrayOf (0, 0, 0, 0, 5, 0, 0, 0, 0 )
        quadMaior [5] = arrayOf (0, 0, 0, 4, 8, 3, 0, 0, 0 )
        quadMaior [6] = arrayOf (0, 0, 3, 0, 7, 0, 4, 0, 0 )
        quadMaior [7] = arrayOf (0, 2, 0, 0, 6, 0, 0, 1, 0 )
        quadMaior [8] = arrayOf (1, 0, 0, 0, 4, 0, 0, 0, 2 )
        arIntQtiNumDisp = arrayOf (6, 6, 6, 6, 8, 6, 6, 6, 6)
         */
        /* Teste 2
        quadMaior [0] = arrayOf (1, 0, 0, 0, 9, 0, 0, 0, 2 )
        quadMaior [1] = arrayOf (0, 2, 0, 0, 8, 0, 0, 1, 0 )
        quadMaior [2] = arrayOf (0, 0, 3, 0, 7, 0, 4, 0, 0 )
        quadMaior [3] = arrayOf (0, 0, 0, 4, 0, 3, 0, 0, 0 )
        quadMaior [4] = arrayOf (0, 0, 0, 0, 5, 0, 0, 0, 0 )
        quadMaior [5] = arrayOf (0, 0, 0, 7, 0, 6, 0, 0, 0 )
        quadMaior [6] = arrayOf (0, 0, 6, 0, 3, 0, 7, 0, 0 )
        quadMaior [7] = arrayOf (0, 9, 0, 0, 2, 0, 0, 8, 0 )
        quadMaior [8] = arrayOf (8, 0, 0, 0, 0, 0, 0, 0, 9 )
        arIntQtiNumDisp = arrayOf (7, 6, 6, 7, 8, 7, 6, 6, 6)
        */
        // </DEBUG>

        //--- Verifica se jogo válido quanto a repetição de números em Qm e/ou Lin e/ou Col.
        //-----------------------------------------------------
        var flagJogoValido = verificaSeJogoValido(quadMaior)
        //-----------------------------------------------------

        when {

            strOpcaoJogo == "JogoGerado" -> flagJogoGeradoOk = flagJogoValido
            strOpcaoJogo.contains("JogoPreSetado") -> flagJogoAdaptadoOk = flagJogoValido
            strOpcaoJogo == "JogoEditado" -> flagJogoEditadoOk = flagJogoValido
            else -> flagJogoGeradoOk = flagJogoValido

        }

        //--- Se não tiver jogo válido, informa ao usuário
        if (!flagJogoValido) {

            //flagJogoGeradoOk = false

            strToast = "Jogo inválido!"
            //--------------------------------------
            utilsKt.mToast(this, strToast)
            //--------------------------------------
            Log.d(cTAG, "-> $strToast")

        }
        //--- Se tiver jogo válido, gera o GABARITO e tenta finalizar a preparação do jogo
        else {

            //-----------------------
            finalizaEdicaoPreset()
            //-----------------------

            //--- GABARITO: jogo gerado
            val arIntNumsGab = ArrayList<Int>()
            for (idxLin in 0..8) {
                for (idxCol in 0..8) {
                    //-------------------------------------------------
                    arIntNumsGab += sgg.quadMaiorRet[idxLin][idxCol]
                    //-------------------------------------------------
                }
            }

            flagJogoValido = (utilsKt.quantZeros(sgg.quadMaiorRet) == 0)

            if (!flagJogoValido) {

                //------------------------------------------------------------------
                utilsKt.mToast(this, "Jogo inválido: gabarito!")
                //------------------------------------------------------------------
                Log.d(cTAG, "-> Jogo inválido: gabarito!")

            }
            else {

                //--- JOGO: envia o jogo preparado para ser jogado
                val arIntNumsJogo = ArrayList<Int>()
                for (idxLin in 0..8) {
                    for (idxCol in 0..8) {
                        //-------------------------------------------
                        arIntNumsJogo += quadMaior[idxLin][idxCol]
                        //-------------------------------------------
                    }
                }

                strToast  = "Jogo válido! Limites:\n"
                strToast += "Erro: ${if (intLimiteErros == -1) " sem" else " $intLimiteErros"}"
                strToast += " Tempo: ${if (strLimiteTempo == "00:00") " sem" else " $strLimiteTempo"}"
                //--------------------------------------
                utilsKt.mToast(this, strToast)
                //--------------------------------------
                Log.d(cTAG, "-> $strToast")

                //--- Prepara a Intent para chamar JogarActivity para JogosGerados e para JogosEditados
                val intent = Intent(this, JogarActivity::class.java)
                intent.action = strOpcaoJogo

                intent.putExtra("strNivelJogo", strNivelJogo)
                intent.putExtra("strSubNivelJogo", edtViewSubNivel.text.toString())
                intent.putExtra("strCronoConta", "00:00")
                intent.putExtra("strErro", "0")

                intent.putIntegerArrayListExtra("GabaritoDoJogo", arIntNumsGab)
                intent.putIntegerArrayListExtra("JogoPreparado", arIntNumsJogo)

                //----------------------
                startActivity(intent)
                //----------------------
            }

        }
    }

    //----------------------------------------------------------------------------------------------
    // Funções para o atendimento de tapping nos radioButtons de escolha de níveis de jogo
    //----------------------------------------------------------------------------------------------
    //--- rbJogoFacil
    fun rbJogoFacil(view: View?) {

        flagJogoGeradoOk = false

        strLog = "-> onClick rbJogoFacil"
        Log.d(cTAG, strLog)

        //-----------------------
        verifMudancaNivel(FACIL)
        //-----------------------

    }

    //--- rbJogoMedio
    fun rbJogoMedio(view: View?) {

        flagJogoGeradoOk = false

        strLog = "-> onClick rbJogoMedio"
        Log.d(cTAG, strLog)

        //-----------------------
        verifMudancaNivel(MEDIO)
        //-----------------------

    }

    //--- rbJogoDificil
    fun rbJogoDificil(view: View?) {

        flagJogoGeradoOk = false

        strLog = "-> onClick rbJogoDificil"
        Log.d(cTAG, strLog)

        //---------------------------
        verifMudancaNivel(DIFICIL)
        //---------------------------

    }

    //--- rbJogoMuitoDificil
    fun rbJogoMuitoDificil(view: View?) {

        flagJogoGeradoOk = false

        strLog = "-> onClick rbJogoMuitoDificil"
        Log.d(cTAG, strLog)

        //---------------------------------
        verifMudancaNivel(MUITO_DIFICIL)
        //---------------------------------

    }

    //----------------------------------------------------------------------------------------------
    // Funções para o atendimento de tapping nos radioButtons de escolha da adaptação de jogo
    //----------------------------------------------------------------------------------------------
    //--- RBpresetClick
    fun RBpresetClick(view: View?) {

        strLog = "-> onClick rbPreset"
        Log.d(cTAG, strLog)

        flagJogoGeradoOk = false

        //-----------------------------
        visibilidadeViews(INVISIBLE)
        //-----------------------------

        //---------------
        adaptaPreset()
        //---------------

    }

    //--- RBedicaoClick
    fun RBedicaoClick(view: View?) {

        strLog = "-> onClick rbEdicao"
        Log.d(cTAG, strLog)

        //---------------
        edicaoPreset()
        //---------------

    }

    //----------------------------------------------------------------------------------------------
    //                           Funções edição de jogos
    //----------------------------------------------------------------------------------------------
    //--- mostraCelAEditar
    private fun mostraCelAEditar(intLinha: Int, intCol: Int) {

        //-----------------------------------------------------
        utilsKt.copiaBmpByBuffer(bmpMyImageBack, bmpMyImage)
        //-----------------------------------------------------

        //--- Pinta de laranja a célula tocada
        canvasMyImage = Canvas(bmpMyImage!!)
        //---------------------------------------------
        pintaCelula(intLinha, intCol, pincelLaranja)
        //---------------------------------------------

    }

    //--- pintaCelula no canvasMyImage
    private fun pintaCelula(intLinha: Int, intCol: Int, pincelPintar: Paint?) {

        //- Canto superior esquerdo do quadrado
        val flXSupEsq = (intCol * intCellwidth).toFloat()
        val flYSupEsq = (intLinha * intCellheight).toFloat()
        //- Canto inferior direito do quadrado
        val flXInfDir = flXSupEsq + intCellwidth.toFloat()
        val flYInfDir = flYSupEsq + intCellheight.toFloat()
        //------------------------------------------------------------------------------------------
        canvasMyImage?.drawRect(
            toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir), toPx(flYInfDir),
            pincelPintar!!
        )
        //------------------------------------------------------------------------------------------
        ivSudokuBoardMain.setImageBitmap(bmpMyImage)

        //--- DEBUG: atualiza a qtidd de númDisp
        //-----------------------
        //atualizaIVQtiNumDisp()
        //-----------------------

    }

    //--- preparaIVNumDisp
    private fun preparaIVNumDisp() {

        //--- Instancializações e inicializações
        var flXSupEsq: Float //  = 0f
        var flYSupEsq: Float //  = 0f

        var flXInfDir: Float //  = 0f
        var flYInfDir: Float //  = 0f

        //--- Torna visíveis as images Views
        //-----------------------------
        visibilidadeViews(VISIBLE)
        //-----------------------------

        //--- Pinta-o e preenche-o
        bmpNumDisp = BitmapFactory.decodeResource(resources, R.drawable.quadro_nums_disp)
            .copy(Bitmap.Config.ARGB_8888, true)
        canvasNumDisp = Canvas(bmpNumDisp!!)

        //- Canto superior esquerdo do retângulo
        flXSupEsq = 0f
        flYSupEsq = 0f

        //- Canto inferior direito do quadrado
        flXInfDir = flXSupEsq + 9 * intCellwidth.toFloat()
        flYInfDir = intCellheight.toFloat()
        //------------------------------------------------------------------------------------------
        canvasNumDisp!!.drawRect(
            toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
            toPx(flYInfDir), pincelVerde
        )
        //------------------------------------------------------------------------------------------

        //--- Desenha-o
        val pincelFino = 2.toFloat()
        val pincelGrosso = 6.toFloat()
        // Linha horizontal superior
        pincelPreto.strokeWidth = pincelGrosso
        flXSupEsq = 0f
        flYSupEsq = 0f
        flXInfDir = (9 * intCellwidth).toFloat()
        flYInfDir = 0f
        //------------------------------------------------------------------------------------------
        canvasNumDisp!!.drawLine(
            toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
            toPx(flYInfDir), pincelPreto
        )
        //------------------------------------------------------------------------------------------
        // Linha horizontal inferior
        flXSupEsq = 0f
        flYSupEsq = intCellheight.toFloat()
        flXInfDir = (9 * intCellwidth).toFloat()
        flYInfDir = flYSupEsq
        //------------------------------------------------------------------------------------------
        canvasNumDisp!!.drawLine(
            toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
            toPx(flYInfDir), pincelPreto
        )
        //------------------------------------------------------------------------------------------
        // Linhas verticais
        for (idxCel in 0..9) {

            pincelPreto.strokeWidth = if (idxCel % 3 == 0) pincelGrosso else pincelFino

            //- Canto superior esquerdo do retângulo
            flXSupEsq = (idxCel * intCellwidth).toFloat()
            flYSupEsq = 0f
            //- Canto inferior direito do quadrado
            flXInfDir = flXSupEsq
            flYInfDir = intCellheight.toFloat()
            //--------------------------------------------------------------------------------------
            canvasNumDisp!!.drawLine(
                toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
                toPx(flYInfDir), pincelPreto
            )
            //--------------------------------------------------------------------------------------

        }

        //--- Escreve os números
        pincelBranco.textSize = intTamTxt * scale
        for (numDisp in 1..9) {

            val strNum = numDisp.toString()
            val idxNum = numDisp - 1

            val yCoord = intCellheight * 3 / 4
            val xCoord = intCellwidth / 3 + idxNum * intCellwidth

            //--------------------------------------------------------------------------------------
            canvasNumDisp!!.drawText(
                strNum, toPx(xCoord.toFloat()), toPx(yCoord.toFloat()),
                pincelBranco
            )
            //--------------------------------------------------------------------------------------

        }

        //--- Apaga os números que já foram utilizados
        for (idxNumDisp in (0..8)) {

            if (arIntQtiNumDisp[idxNumDisp] == 0) {

                //- Canto superior esquerdo do quadrado
                flXSupEsq = idxNumDisp * intCellwidth.toFloat()
                flYSupEsq = 0f

                //- Canto inferior direito do quadrado
                flXInfDir = flXSupEsq + intCellwidth.toFloat()
                flYInfDir = (intCellheight + 1).toFloat()
                //----------------------------------------------------------------------------------
                canvasNumDisp!!.drawRect(
                    toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
                    toPx(flYInfDir), pincelBranco
                )
                //----------------------------------------------------------------------------------

            }

        }
        //--- Atualiza a image view
        ivNumDisp.setImageBitmap(bmpNumDisp)

    }

    //----------------------------------------------------------------------------------------------
    //                              Funções gráficas
    //----------------------------------------------------------------------------------------------
    //--- inicializaObjGraf
    private fun inicializaObjGraf() {

        intTamTxt = 25
        scale = resources.displayMetrics.density

        ivSudokuBoardMain = findViewById(R.id.ivSudokuBoardMain)
        ivNumDisp = findViewById(R.id.imageView3)
        //ivQtiNumDisp         = findViewById(R.id.imageView4)

        //-----------------------------
        visibilidadeViews(INVISIBLE)
        //-----------------------------

        bmpMyImage = BitmapFactory.decodeResource(resources, R.drawable.sudoku_board3)
            .copy(Bitmap.Config.ARGB_8888, true)
        bmpMyImageInic = BitmapFactory.decodeResource(resources, R.drawable.sudoku_board3)
            .copy(Bitmap.Config.ARGB_8888, true)
        bmpMyImageBack = BitmapFactory.decodeResource(resources, R.drawable.sudoku_board3)
            .copy(Bitmap.Config.ARGB_8888, true)

        intCellwidth = bmpMyImage!!.width / 9
        intCellheight = bmpMyImage!!.height / 9

        canvasMyImage?.setBitmap(bmpMyImage!!)

        pincelVerde.color = ContextCompat.getColor(this, R.color.verde)
        pincelBranco.color = ContextCompat.getColor(this, R.color.white)
        pincelPreto.color = ContextCompat.getColor(this, R.color.black)
        pincelAzul.color = ContextCompat.getColor(this, R.color.azul)
        pincelLaranja.color = ContextCompat.getColor(this, R.color.laranja)

    }

    //--- Converte um valor em dp para pixels (px)
    private fun toPx(dip: Float): Float {
        return (dip)
    }

    //----------------------------------------------------------------------------------------------
    // SudokuBoard
    //----------------------------------------------------------------------------------------------
    //--- preencheSudokuBoard7
    private fun preencheSudokuBoard(arArIntJogo: Array<Array<Int>>) {

        //--- Objetos gráficos
        // Paint
        bmpMyImage = BitmapFactory.decodeResource(resources, R.drawable.sudoku_board3)
            .copy(Bitmap.Config.ARGB_8888, true)

        //--- Escreve nas células
        intCellwidth = bmpMyImage!!.width / 9
        intCellheight = bmpMyImage!!.height / 9

        //intCellwidth  = toPx((bmpMyImage!!.width).toFloat()).toInt()  / 9
        //intCellheight = toPx((bmpMyImage!!.height).toFloat()).toInt() / 9

        val canvasMyImage = Canvas(bmpMyImage!!)

        for (intLinha in 0..8) {

            for (intCol in 0..8) {

                //-------------------------------------------
                val intNum = arArIntJogo[intLinha][intCol]
                //-------------------------------------------
                if (intNum > 0) {

                    val strTexto = intNum.toString()

                    pincelAzul.textSize = intTamTxt * scale

                    //--- Coordenada Y (linhas)
                    //--------------------------------------------------------------
                    val yCoord = intCellheight * 3 / 4 + intLinha * intCellheight
                    //--------------------------------------------------------------

                    //--- Coordenada X (colunas)
                    //------------------------------------------------------
                    val xCoord = intCellwidth / 3 + intCol * intCellwidth
                    //------------------------------------------------------

                    //------------------------------------------------------------------------------
                    canvasMyImage.drawText(
                        strTexto, toPx(xCoord.toFloat()), toPx(yCoord.toFloat()),
                        pincelAzul
                    )
                    //------------------------------------------------------------------------------

                    //------------------------------------------------------------------------------
                    //canvasMyImage.drawText(strTexto, xCoord.toFloat(), yCoord.toFloat(),
                    //    pincelAzul)
                    //------------------------------------------------------------------------------
                }
            }
        }
        //-----------------------------------------------------
        utilsKt.copiaBmpByBuffer(bmpMyImage, bmpMyImageBack)
        //-----------------------------------------------------

        ivSudokuBoardMain.setImageBitmap(bmpMyImage)

        //--- DEBUG: atualiza a qtidd de númDisp
        //-----------------------
        //atualizaIVQtiNumDisp()
        //-----------------------

    }

    //--- editaIVSudokuBoard
    private var intNumAtual = 0
    private fun editaIVSudokuBoard(coordX: Int, coordY: Int) {

        //--- OffSets das coordenadas na Janela (???)
        val viewCoords = IntArray(2)

        //--- Determinha linha e coluna do tabuleiro
        ivSudokuBoardMain.getLocationOnScreen(viewCoords)
        //Log.d(cTAG, "viewCoord x: " + viewCoords[0] viewCoord y: " + viewCoords[1])

        //--- Coordenadas reais (???)
        //val imageX = coordX - viewCoords[0] // viewCoords[0] is the X coordinate
        //val imageY = coordY - viewCoords[1] // viewCoords[1] is the y coordinate
        //Log.d(cTAG, "Real x: $imageX Real y: $imageY")

        //--- Coordenadas da célula tocada
        intColJogar = coordX / intCellwidth
        intLinJogar = coordY / intCellheight

        if (intLinJogar < 9 && intColJogar < 9) {

            //-----------------------------------------------
            intNumAtual = arArIntNums[intLinJogar][intColJogar]
            //-----------------------------------------------
            strLog = "-> Celula tocada: linha = " + intLinJogar + ", coluna = " + intColJogar +
                    ", numero = " + intNumAtual
            Log.d(cTAG, strLog)

            //--- Tocou numa célula sem número
            if (intNumAtual == 0) {

                //---------------------------
                editaIvSudokuBoardC1()
                //---------------------------

            }
            //--- Se tocou numa célula com número, solicita ao usuário que confirme sua sobreescrita
            else {

                //-----------------
                verifEdicaoCel()
                //-----------------

            }
        }
    }

    //--- verifEdicaoCel
    private fun verifEdicaoCel() {

        androidx.appcompat.app.AlertDialog.Builder(this)

            .setTitle("Sudoku - Edição")
            .setMessage("Você selecionou uma célula já ocupada!")

            .setPositiveButton("Limpe-a") { _, _ ->

                //Toast.makeText(applicationContext, "OK was pressed", Toast.LENGTH_LONG).show()
                Log.d(cTAG, "-> \"Limpe-a\" was pressed")

                //-------------
                zeraCelula()
                //-------------

            }

            .setNegativeButton("Sobrescreva-a") { _, _ ->

                //Toast.makeText(applicationContext, "Cancel was pressed", Toast.LENGTH_LONG).show()
                Log.d(cTAG, "-> \"Sobrescreva-a\" was pressed")

                //-------------
                zeraCelula()
                //-----------------------
                editaIvSudokuBoardC1()
                //-----------------------

            }

            .setNeutralButton("Esqueça") { _, _ ->

                //Toast.makeText(applicationContext, "Neutral was pressed", Toast.LENGTH_LONG).show()
                Log.d(cTAG, "-> \"Esqueça\" was pressed")

                //--------------
                voltaEdicao()
                //--------------

            }
            .show()

    }

    //--- editaIvSudokuBoard_c1
    private fun editaIvSudokuBoardC1() {

        //-------------------------------------------
        mostraCelAEditar(intLinJogar, intColJogar)
        //-------------------------------------------
        flagBoardSel = true

    }

    //--- zeraCelula
    private fun zeraCelula() {

        //--- Limpa a célula
        // Lê o número atual na célula
        val intNumAtual = arArIntNums[intLinJogar][intColJogar]
        // Limpou a célula
        arArIntNums[intLinJogar][intColJogar] = 0
        // Retornou a fichinha para a caixinha
        if (intNumAtual > 0) arIntQtiNumDisp[intNumAtual - 1]++
        // Atualiza contadores
        val intQtiZeros = utilsKt.quantZeros(arArIntNums)
        val intQtiNums = 81 - intQtiZeros
        tvContaClues.text = intQtiZeros.toString()
        tvContaNums.text = intQtiNums.toString()

        //--------------
        voltaEdicao()
        //--------------

    }

    //--- volta à edição
    private fun voltaEdicao() {

        //--- ivSudokuBoardMain
        //---------------------------------
        preencheSudokuBoard(arArIntNums)
        //---------------------------------

        //--- ivNumDisp
        //-------------------
        preparaIVNumDisp()
        //-------------------

    }

    //----------------------------------------------------------------------------------------------
    // NumDisp
    //----------------------------------------------------------------------------------------------
    //--- editaIVNumDisp
    private fun editaIVNumDisp(coordX: Int) {

        //--- Inicializações e inicializações
        val flXSupEsq: Float
        val flYSupEsq: Float
        val flXInfDir: Float
        val flYInfDir: Float

        //--- Se o jogo nâo está em pausa e nem esperando ser iniciado
        if (flagBoardSel) {

            //--- Se tem célula selecionada no Sudoku Board, trata a edição da célula
            //--- Determina a célula e o número tocado
            val cellX = coordX / intCellwidth
            val intNum = arIntNumsDisp[cellX]

            //--- Célula válida
            if (intLinJogar < 9 && intColJogar < 9 && cellX < 9) {

                //--- Se tocou numa célula já com número; solicita ao usuário que confirme sua sobreescrita.
                if (arArIntNums[intLinJogar][intColJogar] > 0) {

                    //-----------------
                    verifEdicaoCel()
                    //-----------------

                }
                //--- Tocou numa célula ainda sem número
                else {

                    //--- Se ainda tem número desse disponível e ele é válido para o jogo, escreve-o no board
                    if (arIntQtiNumDisp[cellX] > 0) {

                        //--- Verifica se número válido
                        //--------------------------------------------------
                        jogarJogo.arArIntNums = copiaArArInt(arArIntNums)
                        //---------------------------------------------------------
                        val Qm = jogarJogo.determinaQm(intLinJogar, intColJogar)
                        //--------------------------------------------------------------------------
                        val flagNumVal =
                            jogarJogo.verifValidade(Qm, intLinJogar, intColJogar, intNum)
                        //--------------------------------------------------------------------------

                        //--- Se válido e ainda tem número desse disponível escreve-o no board
                        if (flagNumVal) {

                            arIntQtiNumDisp[cellX]--

                            //--- Adiciona-o ao board local
                            arArIntNums[intLinJogar][intColJogar] = intNum
                            //---------------------------------
                            preencheSudokuBoard(arArIntNums)
                            //--------------------------------------------------
                            val intQtiZeros = utilsKt.quantZeros(arArIntNums)
                            //--------------------------------------------------
                            tvContaClues.text = intQtiZeros.toString()
                            tvContaNums.text = (81 - intQtiZeros).toString()

                            flagJogoAdaptadoOk = (intQtiZeros in 20..59)

                            //--- Sinaliza se já posicionou os 9 desse número
                            if (arIntQtiNumDisp[cellX] == 0) {

                                //- Canto superior esquerdo do quadrado
                                flXSupEsq = cellX * intCellwidth.toFloat()
                                flYSupEsq = 0f

                                //- Canto inferior direito do quadrado
                                flXInfDir = flXSupEsq + intCellwidth.toFloat()
                                flYInfDir = (intCellheight + 1).toFloat()
                                //------------------------------------------------------------------
                                canvasNumDisp!!.drawRect(
                                    toPx(flXSupEsq), toPx(flYSupEsq), toPx(flXInfDir),
                                    toPx(flYInfDir), pincelBranco
                                )
                                //------------------------------------------------------------------
                                ivNumDisp.setImageBitmap(bmpNumDisp)

                            }

                            flagBoardSel = false

                        }
                        //--- Número NÃO válido
                        else {

                            strToast = "Número NÃO OK!\n(linha, coluna ou quadro)"
                            //--------------------------------------
                            utilsKt.mToast(this, strToast)
                            //--------------------------------------

                        }

                        //----------------------------------------------
                        quadMaior = utilsKt.copiaArArInt(arArIntNums)
                        //----------------------------------------------

                        //--- Seta flag se jogo em edição for válido
                        //----------------------------------------------------
                        flagJogoEditadoOk = verificaSeJogoValido(quadMaior)
                        //----------------------------------------------------

                    }
                }
            }
            //--- Célula NÃO válida
            else {

                strToast = "Célula NÃO válida (l = $intLinJogar, c = $intColJogar)!"
                Toast.makeText(this, strToast, Toast.LENGTH_LONG).show()

            }

        }
        //--- Não há célula selecionada no Sudoku Board
        else {

            //--------------------------------------------------------------------------------------
            Toast.makeText(
                this, "Selecione uma célula no Sudoku board!",
                Toast.LENGTH_LONG
            ).show()
            //--------------------------------------------------------------------------------------

        }
    }

    //----------------------------------------------------------------------------------------------
    //                                      Funções
    //----------------------------------------------------------------------------------------------
    //--- visibilidadeViews
    private fun visibilidadeViews(visibilidade: Int) {

        //--- DEBUG
        //ivQtiNumDisp.visibility = INVISIBLE  //visibilidade

        ivNumDisp.visibility = visibilidade
        tvContaClues.visibility = visibilidade
        tvContaNums.visibility = visibilidade

        findViewById<TextView>(R.id.legContaNums).visibility = visibilidade
        findViewById<TextView>(R.id.legContaClues).visibility = visibilidade

    }

    //--- copiaArArInt
    private fun copiaArArInt(arArIntPreset: Array<Array<Int>>): Array<Array<Int>> {

        /* https://stackoverflow.com/questions/45199704/kotlin-2d-array-initialization
            // A 6x5 array of Int, all set to 0.
            var m = Array(6) {Array(5) {0} }
         */

        //------------------------------------------------------
        val arArIntTmp = Array(9) { Array(9) { 0 } }
        //------------------------------------------------------

        for (intLin in 0..8) {
            for (intCol in 0..8) {
                arArIntTmp[intLin][intCol] = arArIntPreset[intLin][intCol]
            }
        }

        return arArIntTmp

    }

    //--- Prepara rbNivel
    private fun prepRBniveis(habOuDesab: Boolean) {

        val intIdxChild = groupRBnivel.childCount - 1
        if (intIdxChild > 0) {

            for (idxRB in 0..intIdxChild) {
                groupRBnivel.getChildAt(idxRB).isEnabled = habOuDesab
            }

        }
    }

    //--- verifMudancaNivel
    private fun verifMudancaNivel(intNivel: Int) {

        if (strOpcaoJogo == "JogoGerado") {

            if (edtViewSubNivel.text.toString().isNotEmpty()) {

                val intNivelTotal = intNivel + edtViewSubNivel.text.toString().toInt()
                if (intNivelTotal != nivelJogo) {

                    flagJogoGeradoOk = false
                    flagJogoAdaptadoOk = false

                    val arArIntJogo = Array(9) { Array(9) { 0 } }
                    //---------------------------------
                    preencheSudokuBoard(arArIntJogo)
                    //---------------------------------

                    nivelJogo = intNivelTotal

                }
            } else Toast.makeText(
                this, "Não é possível gerar o jogo sem subnivel!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //----------------------------------------------------------------------------------------------
    //                                     Permissões
    //----------------------------------------------------------------------------------------------
    //--- Declara o retorno para PermissionsResult
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 || requestCode == ALL_FILES_ACCESS_PERMISSION) {

            val lstPermNaoOk: MutableList<String> = java.util.ArrayList()
            var strLogRes = "Permissions: "
            for (intIdx in permissions.indices) {

                strLogRes += """
                
                ${permissions[intIdx]}=${grantResults[intIdx]} """.trimIndent()

                if (grantResults[intIdx] == -1) lstPermNaoOk.add(permissions[intIdx])

            }

            Log.d(cTAG, strLogRes)

            val flagInstalacaoOk = lstPermNaoOk.isEmpty()
            var flagDefault      = false
            strLog = when (flagInstalacaoOk) {
                //if  (flagInstalacaoOk) {

                true -> {

                    flagPermissoesOk = true

                    //-------------------------------
                    //flagLeitSetUpOK = leArqSetUp()
                    //-------------------------------

                    //--- Leitura Ok, usa os valores lidos
                    if (leArqSetUp()) {

                        intLimiteErros      = intLimiteErrosAtual
                        strLimiteTempo      = strLimiteTempoAtual
                        flagMostraNumIguais = flagMostraNumIguaisAtual

                    }
                    //--- Leitura NÃO ok, usa valores default
                    else {

                        flagDefault = true

                        strToast  = "Leitura set up não Ok! Usa default"
                        utilsKt.mToast(this, strToast)
                        Log.d(cTAG, "-> $strToast")

                        intLimiteErrosAtual = -1
                        intLimiteErros      = intLimiteErrosAtual

                        strLimiteTempoAtual = "00:00"
                        strLimiteTempo      = strLimiteTempoAtual

                        flagMostraNumIguaisAtual = true
                        flagMostraNumIguais      = flagMostraNumIguaisAtual

                    }

                    "- Permissão concedida! Params: ${if (flagDefault) "default" else "setup"}"

                }
                false -> {
                    flagPermissoesOk = false
                    "- Permissão NÃO concedida!"
                }

            }

            Log.d(cTAG, strLog)
            utilsKt.mToast(this, strLog)

            if (!flagInstalacaoOk && (permissions[0] == "android.permission.WRITE_EXTERNAL_STORAGE")) {
                var strMsg = "O Android NÃO concedeu acesso ao salvamento de jogos."
                strMsg += "\nProvável problema: API incompatível."
                strMsg += "\n\nContinua o App ou pára?"
                androidx.appcompat.app.AlertDialog.Builder(this)

                    .setTitle("Sudoku - Inicia App")
                    .setMessage(strMsg)

                    .setPositiveButton("Continua App") { _, _ ->

                        Log.d(cTAG, "-> O usuário optou por continuar sem salvamento.")

                    }

                    .setNegativeButton("Pára App") { _, _ ->

                        Log.d(cTAG, "-> O usuário optou por finalizar o App.")

                        //---------
                        finish()
                        //---------

                    }
                    .show()
            }
        }

    }

    //---------------------------------------------------------------------
    // Verifica se Permissions do Manifest estão granted
    //---------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.R)
    //private fun verifPermissoesAcessoAPI(): Boolean {
    private fun verifPermissoesAcessoAPI() {

        var strMsgDebug = "-> main Verifica permissões"
        Log.d(cTAG, strMsgDebug)

        //---------------------------------------------------------
        flagPermissoesOk = utils.VerificaPermissoes(this)
        //---------------------------------------------------------

        if (!flagPermissoesOk) {

            strMsgDebug = "Permission unGranted\nAguarde ..."

            utilsKt.mToast(this, strMsgDebug)

            Log.d(cTAG, "-> $strMsgDebug")

        }

        else {

            strMsgDebug = "Permission Granted!"
            Log.d(cTAG, strMsgDebug)

        }

        //--- Se precisar solicita ao jogador que autorize pelo config a utilização do ScopedStorage
        if (!flagScopedStorage && utils.permScopedStorage.isNotEmpty()) {

            //--- Solicita ativação no config para o jogador habilitar o Scoped Storage desse App
            val builder = AlertDialog.Builder(this)
            builder
                .setTitle("Tip")
                .setMessage("O Sudoku para Android A$versAndroid precisa de sua autorização")
                .setPositiveButton("OK") { _, _ ->

                    flagScopedStorage = true

                    val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)

                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    intent.data = uri
                    startActivity(intent)

                }

                .setNegativeButton("Não") { _, _ ->

                }

            builder.show()
        }

        //--- Se ainda não foi concedida a permissão, aguarda permissões Ok
        val waitTime = if (!flagPermissoesOk) 10000L else 100L  // milisegundos
        Handler(Looper.getMainLooper()).postDelayed(
            {
                //------------------------
                finalizaInicializacao()
                //------------------------
            },
            waitTime

        )  // value in milliseconds

        //return flagPermissoesOk

    }

    //----------------------------------------------------------------------------------------------
    //                               Funções Jogo selecionado
    //----------------------------------------------------------------------------------------------

    //--- adaptaPreset
    private fun adaptaPreset() {

        // --- Prepara o preset para se conseguir o gabarito do jogo
        //- Continua a adaptação após um tempo para atualização da UI (progress bar e txtDadosJogo)
        val waitTime = 100L  // milisegundos
        Handler(Looper.getMainLooper()).postDelayed(
            {

                //-----------------------------
                visibilidadeViews(INVISIBLE)
                //-----------------------------
                groupRBadapta.visibility = VISIBLE

                rbPreset.isChecked = true

                //--- Desativa o progress bar
                //progressBar.dismiss()
                //progressBar.visibility = INVISIBLE
                layout.visibility = INVISIBLE

                //--- Ativa a activity de adaptação de jogos
                //-----------------------
                ativaAdaptarActivity()
                //-----------------------

            },
            waitTime
        )  // value in milliseconds
    }

    //--- ativaAdaptarActivity
    private fun ativaAdaptarActivity() {

        //--- Prepara o preset para conseguir o gabarito do jogo
        strLog = "-> Presset is checked"
        Log.d(cTAG, strLog)

        //https://stackoverflow.com/questions/63435989/startactivityforresultandroid-content-intent-int-is-deprecated
        //--- Prepara o startActivityForResult atual
        try {

            //--- Prepara a Intent para chamar AdaptarActivity
            // Versão anterior
            //val intent = Intent(this, PreviewFullscreenActivity::class.java)
            //intent.putStringArrayListExtra(AppConstants.PARAMS.IMAGE_URIS, list)

            // Versão Kotlin
            val intent = Intent(this, AdaptarActivity::class.java)
            intent.action = "InstanciaRVJogosSalvos"
            //------------------------------
            previewRequest.launch(intent)
            //------------------------------

            Log.d(cTAG, "-> Launch AdaptarActivity")

        } catch (exc: Exception) {

            Log.d(cTAG, "-> Erro03: ${exc.message}")

        }

    }

    //--- edicaoPreset()
    private fun edicaoPreset() {

        rbEdicao.isChecked = true

        flagJogoGeradoOk = false
        flagJogoEditadoOk = false

        txtDadosJogo.text = ""

        tvContaNums.text = "0"
        tvContaClues.text = resources.getString(R.string.valor81)

        arArIntNums = Array(9) { Array(9) { 0 } }
        arIntQtiNumDisp = Array(9) { 9 }

        ivNumDisp.isEnabled = true
        ivSudokuBoardMain.isEnabled = true

        //------------------------------------------------------------------------------------------
        // Image view dos números disponíveis
        //------------------------------------------------------------------------------------------
        //-------------------
        preparaIVNumDisp()
        //-------------------

        //------------------------------------------------------------------------------------------
        // Image view do jogo
        //------------------------------------------------------------------------------------------
        bmpMyImage = BitmapFactory.decodeResource(resources, R.drawable.sudoku_board3)
            .copy(Bitmap.Config.ARGB_8888, true)
        ivSudokuBoardMain.setImageBitmap(bmpMyImage)

        //-----------------------------------------------------
        utilsKt.copiaBmpByBuffer(bmpMyImage, bmpMyImageBack)
        //-----------------------------------------------------

        //--- DEBUG: atualiza a qtidd de númDisp
        //-----------------------
        // atualizaIVQtiNumDisp()
        //-----------------------

    }

    //--- finalizaEdicaoPreset
    private fun finalizaEdicaoPreset() {

        //---------------------------
        visibilidadeViews(VISIBLE)
        //---------------------------

        //------------------------------------------------
        val intQtiZeros = utilsKt.quantZeros(quadMaior)
        //------------------------------------------------

        val intNivel    = intQtiZeros / 10
        val intSubNivel = intQtiZeros % 10
        //---------- ---------- ----------------------
        // intNivel     Nivel      números / Zeros
        //---------- ---------- ----------------------
        //    2       fácil     de: 61 / 20  a 52 / 29
        //    3       médio     de: 51 / 30  a 42 / 39
        //    4       difícil   de: 41 / 40  a 32 / 49
        //    5       muito dif de: 31 / 50  a 22 / 59

        when {

            intQtiZeros > 59 -> {
                strToast = "Para gerar jogo editar mais do que 21 números!"
            }
            intQtiZeros < 20 -> {
                strToast = "Para gerar jogo editar menos do que 62 números!"
            }
            else -> {

                strNivelJogo = when (intNivel) {

                    2 -> {
                        rbFacil.isChecked = true
                        arStrNivelJogo[idxFACIL]
                    }
                    3 -> {
                        rbMedio.isChecked = true
                        arStrNivelJogo[idxMEDIO]
                    }
                    4 -> {
                        rbDificil.isChecked = true
                        arStrNivelJogo[idxDIFICIL]
                    }
                    else -> {
                        rbMuitoDificil.isChecked = true
                        arStrNivelJogo[idxMUITO_DIFICIL]
                    }
                }
            }

        }

        edtViewSubNivel.setText(intSubNivel.toString())

        //strToast = "Jogo:\n- nível: $strNivelJogo subnível: $intSubNivel"
        //utilsKt.mToast(this, strToast)

        val flagEdicaoOK = true

        //--- Se edição OK gera o gabarito para o jogo editado
        if (flagEdicaoOK) {

            sgg.quadMaiorRet = copiaArArInt(quadMaior)
            arArIntNums      = copiaArArInt(quadMaior)
            //---------------------------------------
            quadMaior = sgg.adaptaJogoAlgoritmo2()
            //---------------------------------------

        }

        // **** O array preparado (quadMaior) e o gabarito (sgg.quadMaiorRet) serão enviados
        // **** pelo listener do botão JogaJogo.
    }

    //--- verificaSeJogoValido
    fun verificaSeJogoValido(arArJogo: Array<Array<Int>>): Boolean {

        var flagJogoValido = false

        //--- Quantidade de clues entre 20 e 59   (Fácil: 2.0 e MuitoDifícil: 5.9)
        val qtizeros = utilsKt.quantZeros(arArJogo)
        // Quantidade de zeros menos que 20 ou mais do que 59
        if ((strOpcaoJogo == "JogoGerado" || strOpcaoJogo == "JogoEditado") &&
            (qtizeros < 20 || qtizeros > 59)
        ) return false
        // Quantidade de zeros entre 20 e 59
        else {

            //--- Confere números nos Qm, linhas e colunas
            for (idxLin in 0..8) {

                for (idxCol in 0..8) {

                    val intNum = arArJogo[idxLin][idxCol]
                    if (intNum > 0) {

                        arArJogo[idxLin][idxCol] = 0

                        // Determina a que Qm a célula pertence
                        //---------------------------------------------------------
                        val intQuadMenor = jogarJogo.determinaQm(idxLin, idxCol)
                        //---------------------------------------------------------

                        // Verifica se o número dessa célula não existe no seu Qm, na sua linha e na
                        // sua coluna.

                        jogarJogo.arArIntNums = copiaArArInt(arArJogo)  // <<<<<<<<< ATENÇÃO !!!!
                        //--------------------------------------------------------------------------
                        flagJogoValido =
                            jogarJogo.verifValidade(intQuadMenor, idxLin, idxCol, intNum)
                        //--------------------------------------------------------------------------

                        arArJogo[idxLin][idxCol] = intNum

                    }
                }
            }

            /*
            if (flagJogoValido) {

                quadMaior   = copiaArArInt(arArJogo)
                arArIntNums = copiaArArInt(arArJogo)

            }
            */

        }
        return flagJogoValido

    }

    //--------------------------------------------------------------------------
    //                Funções para o tratamento de Parametros
    //--------------------------------------------------------------------------

    //--- implDialogView_AlertDialog para o ajuste dos parâmetros do Jogo
    @SuppressLint("SetTextI18n")
    private fun implDialogViewAlertDialog() { //subMenuLimite : MenuItem) {

        //------------------------------------------------------------------------------------------
        // Implementa o Dialog View
        //------------------------------------------------------------------------------------------
        // Inflate o layout e instancía os controles do layout que farão parte do AlertDialog
        val dialogView = layoutInflater.inflate(R.layout.alertdialog_param_jogo, null)
        edtLimErros = dialogView.findViewById(R.id.edtErros)
        edtLimTempo = dialogView.findViewById(R.id.edtTempo)

        edtLimErros.setText(intLimiteErros.toString())
        edtLimTempo.setText(strLimiteTempo)

        //---------------------------------------------------------------------
        // Prepara o AlertDialog
        //---------------------------------------------------------------------
        val customTitle = TextView(this)
        customTitle.text = "Parâmetros do Jogo"
        customTitle.setBackgroundColor(Color.DKGRAY)
        customTitle.setPadding(10, 10, 10, 10)
        customTitle.gravity = Gravity.CENTER
        customTitle.setTextColor(Color.WHITE)
        customTitle.textSize = 20f

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setCustomTitle(customTitle)
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Ok") { _, _ ->

                Log.d(cTAG, "-> Novos ajustes:")

                // Erros
                var ajusteLim = edtLimErros.text.toString()
                var intLimTmp = 0
                if (ajusteLim.isNotEmpty()) intLimTmp = ajusteLim.toInt()
                // Erro = 0 não é válido
                if (intLimTmp == 0) {

                    strToast = "Limite de erros = 0 é inválido!"
                    utilsKt.mToast(this, strToast)
                    Log.d(cTAG, "-> $strToast")

                }
                // Erro diferente de 0 é válido
                else {

                    intLimiteErros = intLimTmp
                    Log.d(cTAG, "   - erros: $intLimiteErros")

                    // Tempo de jogo
                    ajusteLim = edtLimTempo.text.toString()
                    if (ajusteLim.isNotEmpty()) {

                        if (timeVal.validate(ajusteLim)) {

                            if (ajusteLim.isNotEmpty()) strLimiteTempo = ajusteLim
                            Log.d(cTAG, "   - tempo: $strLimiteTempo")

                        } else {

                            strToast = "Formato incorreto. Entre com \"MM:ss\""

                            utilsKt.mToast(this, strToast)

                            Log.d(cTAG, "-> $strToast")

                        }

                    }

                    //-------------------------
                    atualizaTitlesSubMenus()
                    //-------------------------

                }

            }
            .setNegativeButton("Cancel") { _, _ ->

                Log.d(cTAG, "-> Cancelou ajustes dos limites de Erros e Tempo")

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

    //--- atualizaTitlesSubMenus
    private fun atualizaTitlesSubMenus() { //(salvaParams: Boolean) {

        try {

            //--------------------------------------------------------------------------------------
            //                    Leitura dos parâmetros no arquivo de setup
            //--------------------------------------------------------------------------------------
            //--- Lê o arquivo de setup
            //-------------
            //leArqSetUp()
            //-------------

            //--------------------------------------------------------------------------------------
            //                           Atualiza os submenus
            //--------------------------------------------------------------------------------------
            strToast = ""

            //--- Erros
            strTitleErros = TITLE_ERROS

            if (intLimiteErros != intLimiteErrosAtual) {

                strToast += "   - erros: $intLimiteErros"
                //intLimiteErrosAtual = intLimiteErros

            }
            strTitleErros += if (intLimiteErros == -1) " sem (-1)" else "$intLimiteErros"

            //--- Tempo de jogo
            strTitleTempo = TITLE_TEMPO
            if (strLimiteTempoAtual != strLimiteTempo) {

                strToast += "${if (strToast.isNotEmpty()) "\n" else ""}   - tempo: $strLimiteTempo"
                //strLimiteTempoAtual = strLimiteTempo

            }
            strTitleTempo += if (strLimiteTempo == "00:00") " sem (00:00)" else " $strLimiteTempo"

            //--- Mostrar números iguais
            if (flagMostraNumIguaisAtual != flagMostraNumIguais) {

                strToast += if (strToast.isNotEmpty()) "\n" else ""
                strToast += "   - mostrar nºs =s: $flagMostraNumIguais"
                //flagMostraNumIguaisAtual = flagMostraNumIguais
            }

            // Se há novos ajustes, atualiza o arquivo de parâmetros
            if (strToast.isNotEmpty()) {

                utilsKt.mToast(this, "Novos ajustes:\n$strToast")

                //---------------------------------------------------------------------
                atualizaSubMenu()  //intLimiteErros, strLimiteTempo, flagMostraNumIguais)
                //---------------------------------------------------------------------

            }

            Log.d(cTAG, "-> atualiza titles dos submenus Ok!")

        } catch (exc: Exception) {

            Log.d(cTAG, "-> Erro04: ${exc.message}")

        }

    }

    //--- atualizaMenu
    private fun atualizaSubMenu() {

        var strTmp = "$TITLE_ERROS${if (intLimiteErros == -1) " -1 (sem)" else "$intLimiteErros"}"
        val subMenuLimiteQtiErros = myMenuItem.subMenu.findItem(R.id.action_ajustarQtiddErros)
        //-------------------------------------
        subMenuLimiteQtiErros.title = strTmp
        //-------------------------------------

        strTmp = "$TITLE_TEMPO${if (strLimiteTempo == "00:00") "00:00 (sem)" else strLimiteTempo}"
        val subMenuLimiteTempoJogo = myMenuItem.subMenu.findItem(R.id.action_ajustarTempoDeJogo)
        //--------------------------------------
        subMenuLimiteTempoJogo.title = strTmp
        //--------------------------------------

        strTmp = if (flagMostraNumIguais) "Mostrar números iguais" else "Não Mostrar números iguais"
        val subMenuMostrarNumsIguais = myMenuItem.subMenu.findItem(R.id.action_mostrar_numeros_iguais)
        //----------------------------------------
        subMenuMostrarNumsIguais.title = strTmp
        //----------------------------------------

        Log.d(cTAG, "-> atualiza submenus Ok!")

        if ((intLimiteErrosAtual != intLimiteErros) || (strLimiteTempoAtual != strLimiteTempo) ||
            (flagMostraNumIguaisAtual != flagMostraNumIguais)) {

            //-------------------
            atualizaArqSetUp()
            //-------------------

            intLimiteErrosAtual      = intLimiteErros
            strLimiteTempoAtual      = strLimiteTempo
            flagMostraNumIguaisAtual = flagMostraNumIguais

        }
    }

    //--- atualizaArqSetUp
    private var strModelo = ""
    private fun atualizaArqSetUp() {

        var strConteudo: String

        Log.d(cTAG, "-> Atualiza arquivo de setup")

        //--- Lê o arquivo modelo do setup
        val strNomeArqSemExt = "modelo_arq_xml_sudoku_setup"
        Log.d(cTAG, "   - lê arquivo modelo $strNomeArqSemExt")

        //----------------------------------------------------------------------
        val arStrLeitArqRaw : ArrayList<String> = utils.LeituraFileRaw(this, strNomeArqSemExt)
        //----------------------------------------------------------------------
        for (idxDecl in 0 until arStrLeitArqRaw.size) { strModelo += arStrLeitArqRaw[idxDecl] }

        Log.d(cTAG, "     $strModelo")

        //--- Prepara o conteúdo do arquivo
        Log.d(cTAG, "-> Prepara conteudo a salvar conforme o modelo lido.")

        // Preenche os campos
        var intTag = 0
        val strTag: String
        try {

            //- header / id
            intTag    = 0
            intIdxFim = 0
            //----------------------------------------------------------------------
            strConteudo = preencheConteudo("<id>", "modeloArqXmlSudokuSetUp.txt")
            //----------------------------------------------------------------------

            //-	<body> / <limite_erros>
            intTag = 1
            //----------------------------------------------------------------------------------
            strConteudo += preencheConteudo("<limite_erros>", intLimiteErros.toString())
            //----------------------------------------------------------------------------------

            //-	<body> / <limite_tempo>
            intTag = 2
            //------------------------------------------------------------------------
            strConteudo += preencheConteudo("<limite_tempo>", strLimiteTempo)
            //------------------------------------------------------------------------

            //-	<body> / <mostra_nums_iguais>true</mostra_nums_iguais>
            intTag = 3
            //--------------------------------------------------------------------------------------
            strConteudo += preencheConteudo("<mostra_nums_iguais>",
                                                    if (flagMostraNumIguais) "true" else "false")
            //--------------------------------------------------------------------------------------

            //-- Finaliza a preparação do conteúdo
            intTag = 4
            strConteudo += strModelo.substring(intIdxFim, strModelo.length)

        } catch (exc: Exception) {

            strTag = intTag.toString()
            utilsKt.mToast(this, "Erro $strTag: ${exc.message}")
            strConteudo = ""
        }

        Log.d(cTAG, "   $strConteudo")

        //--- Salva o arquivo setup
        val strArqPath  = "/sudoku/setup"
        val strArqSetUp = "SudokuSetUp.xml"
        //------------------------------------------------------------------------------------------
        val flagEscrita = utils.escExtMemTextFile(this, strArqPath, strArqSetUp, strConteudo)
        //------------------------------------------------------------------------------------------

        strToast = "Escrita arquivo $strArqSetUp ${if (flagEscrita) "OK!" else "NÃO ok!"}"
        utilsKt.mToast(this, strToast)

        strLog  = "-> Escrita arquivo storage/emulated/0/Download$strArqPath/$strArqSetUp "
        strLog += if (flagEscrita) "OK!" else "NÃO ok!"
        Log.d(cTAG, strLog)

    }

    //--- preencheConteudo
    private var intIdxFim = 0
    private fun preencheConteudo(strTag: String, strConteudoTag: String): String {

        var strConteudoPreenchido = ""

        val intIdxInic = intIdxFim
        intIdxFim      = strModelo.indexOf(strTag, intIdxInic, false)
        intIdxFim     += strTag.length

        strConteudoPreenchido += strModelo.substring(intIdxInic, intIdxFim )
        strConteudoPreenchido += strConteudoTag

        return strConteudoPreenchido

    }

    //--- leArqSetUp
    private fun leArqSetUp() : Boolean {

        Log.d(cTAG, "-> leitura arquivo setup")

        var flagLeitOk = true

        try {

            //- Leitura do Arquivo
            val strDir     = "sudoku/setup/"
            val strArqName = "sudokusetup.xml"
            //-----------------------------------------------------
            val strLeitArq = utilsKt.leitArq(strDir, strArqName)
            //-----------------------------------------------------

            //- 1- obtém limite de erros:
            var strTagInic = "limite_erros"
            var strTagFim = "/$strTagInic"
            //--------------------------------------------------------------------------------------//////
            var strCampo = (utilsKt.leCampo(
                strLeitArq, "<$strTagInic>",
                "<$strTagFim>"
            )).trim()
            //---------------------------------------------------------------------------===========
            strLog = "   - limite: $strCampo"
            Log.d(cTAG, strLog)

            intLimiteErrosAtual = strCampo.toInt()

            //- 2- obtém tempo de jogo:
            strTagInic = "limite_tempo"
            strTagFim = "/$strTagInic"
            //--------------------------------------------------------------------------------------
            strCampo = (utilsKt.leCampo(
                strLeitArq, "<$strTagInic>",
                "<$strTagFim>"
            )).trim()
            //--------------------------------------------------------------------------------------
            strLog = "   - limite: $strCampo"
            Log.d(cTAG, strLog)

            strLimiteTempoAtual = strCampo

            //- 3- obtém flag se mostra números iguais:
            strTagInic = "mostra_nums_iguais"
            strTagFim = "/$strTagInic"
            //--------------------------------------------------------------------------------------
            strCampo = (utilsKt.leCampo(
                strLeitArq, "<$strTagInic>",
                "<$strTagFim>"
            )).trim()
            //--------------------------------------------------------------------------------------
            strLog = "   - mostra nums iguais: $strCampo"
            Log.d(cTAG, strLog)

            flagMostraNumIguaisAtual = (strCampo == "true")

        } catch (exc : Exception) {

            Log.d(cTAG, "-> Erro na leitura do arquivo!")

            utilsKt.mToast (this, "Erro na leitura do arquivo!\nPor favor aguarde ...")

            flagLeitOk = false

            /*
            //------------------------------------------------------------------
            // Salva o arquivo
            //------------------------------------------------------------------
            val strArqPath  = "/sudoku/setup"
            val strArqSetUp = "sudokusetup.xml"
            var strConteudo = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            strConteudo    += "<setup><header><id>modeloArqXmlSudokuSetUp.txt</id></header>"
            strConteudo    += "<body><limite_erros>-1</limite_erros>"
            strConteudo    += "<limite_tempo>00:00</limite_tempo>"
            strConteudo    += "<mostra_nums_iguais>true</mostra_nums_iguais></body></setup>"
            //--------------------------------------------------------------------------------------
            val flagEscrita = utils.escExtMemTextFile(
                this, strArqPath, strArqSetUp,
                strConteudo
            )
            //--------------------------------------------------------------------------------------

            strToast = "Escrita arquivo $strArqSetUp "
            strToast += if (flagEscrita) "OK!" else "NÃO ok!"
            utilsKt.mToast(this, strToast)

            strLog = "-> Escrita arquivo storage/emulated/0/Download$strArqPath/$strArqSetUp "
            strLog += if (flagEscrita) "OK!" else "NÃO ok!"
            Log.d(cTAG, strLog)
            */
        }

        return flagLeitOk

    }

    //----------------------------------------------------------------------------------------------
    //                             CounterDown Timer
    //----------------------------------------------------------------------------------------------
    /*
    //--- startTimer
    private fun startTimer(lgTimeOutMs: Long, intTimeTicksMs: Int) {

        ctimer = object : CountDownTimer(lgTimeOutMs, intTimeTicksMs.toLong()) {

            //--- Ticks
            override fun onTick(millisUntilFinished: Long) {


            }

            //--- Finish
            override fun onFinish() {

                Log.d(cTAG, "-> TimeOut de ${(lgTimeOutMs / 1000L)} seg")

                //--------------------
                timeOutPermissoes()
                //--------------------

            }

        }

        //----------------
        ctimer.start()
        //----------------
    }

    //cancel timer
    private fun cancelTimer() { if (ctimer != null) ctimer.cancel() }

    //--- timeOutPermissoes
    private fun timeOutPermissoes() {

        // ToDo

    }
     */

}
