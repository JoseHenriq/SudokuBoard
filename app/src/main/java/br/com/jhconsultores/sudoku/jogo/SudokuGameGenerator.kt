package br.com.jhconsultores.sudoku.jogo

import android.annotation.SuppressLint
import android.util.Log
import br.com.jhconsultores.sudoku.jogo.SudokuBackTracking.intNumBackTracking
import br.com.jhconsultores.utils.UtilsKt

import br.com.jhconsultores.sudoku.ui.MainActivity.Companion.ALGORITMO_JH
import br.com.jhconsultores.utils.GFG
import java.util.*

class SudokuGameGenerator {

    //--------------------------------------------------------------------------
    //                   Instancializações e inicializações
    //--------------------------------------------------------------------------
    private val cTAG   = "Sudoku"
    private var strLog = ""
    
    var txtDados = ""

    var quadMaiorRet = arrayOf<Array<Int>>()
    private var intJogoAdaptar = 0
    private var intQtiZeros    = 0

    private var sggFlagJogoGeradoOk   = false
    private var sggFlagJogoAdaptadoOk = false

    //private var arArSalvaJogoGer = Array(9) { Array(9) {0} }
    private var arArIntNums      = Array(9) { Array(9) {0} }
    private var arArIntSolAlg2   = Array(9) { Array(9) {0} }

    //--- Classes externas
    private val utilsKt = UtilsKt ()

    //--------------------------------------------------------------------------
    //             Gera Jogos (preset int[9][9] = { 0, 0, ..., 0 })
    //--------------------------------------------------------------------------
    //--- GeraJogo
    @SuppressLint("SetTextI18n")
    fun geraJogo(nivelJogo : Int, strAlgoritmo : String) : Array<Array<Int>> {

        sggFlagJogoGeradoOk = false

        var contaTentaJogo  = 0
        val limTentaJogo    = 150
        var flagQuadMenorOk : Boolean

        //-----------------------
        zeraQuadMaiorGeracao()
        //-----------------------

        while (!sggFlagJogoGeradoOk && contaTentaJogo < limTentaJogo) {

            //Log.d(cTAG, "-> Gera o jogo ${contaTentaJogo + 1}")

            //------------------------------------------------------------------
            //                         Gera o jogo
            //------------------------------------------------------------------
            if (strAlgoritmo == ALGORITMO_JH) {

                for (quad in 0..8) {

                    var array = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

                    flagQuadMenorOk = false
                    var numTentaGeracao = 0
                    while (!flagQuadMenorOk && numTentaGeracao < 50) {

                        //============================
                        array = geraQuadMenor(quad)
                        //============================

                        if (!array.contains(0) && !array.contains(-1)) flagQuadMenorOk = true
                        else {
                            numTentaGeracao++
                        }

                    }

                    //--- Insere esse Qm em 'quadMaiorRet'
                    //==========================
                    insereQmEmQM(quad, array)
                    //==========================

                }

            }

            //--- ALGORITMO_SCOTT
            else {

                //==================================
                quadMaiorRet = geraJogoAlgScott()
                //==================================

                return quadMaiorRet

            }

            //------------------------------------------------------------------
            //         Verifica se o jogo Gerado é Válido qto a lin, Col e Qn
            //------------------------------------------------------------------
            var flagJogoVal = true
            for (idxLinhaQM in 0..8) {

                for (idxColQM in 0..8) {

                    val valCel = quadMaiorRet[idxLinhaQM][idxColQM]
                    if (valCel <= 0 || valCel > 9) flagJogoVal = false

                    if (!flagJogoVal) break
                }

                if (!flagJogoVal) break
            }

            //------------------------------------------------------------------
            // Se válido, prepara o Jogo, gera a solução com o algoritmo
            // BaeckTrack2 e verif se UNIQUE.
            //------------------------------------------------------------------
            if (flagJogoVal) {

                flagJogoVal = false
                val strTmp = "-> Jogo ${contaTentaJogo + 1}: válido!"
                Log.d(cTAG, strTmp)

                txtDados = ""

                //------------------------------------
                listaQM(quadMaiorRet, false)
                //------------------------------------
                sggFlagJogoGeradoOk = true

                //-----------------------------------------
                arArIntNums = copiaArArInt(quadMaiorRet)   // quadMaiorRet: jogo gerado (gabarito)
                //-----------------------------------------

                //------------------------------------------------------------------
                //                           Prepara o jogo
                //------------------------------------------------------------------
                //--- Prepara arArIntNums para o jogo: deixa com zeros onde o usuário irá jogar;
                //    a qti de zeros será tão maior quanto o grau de dificuldade for maior.
                //-----------------------
                preparaJogo(nivelJogo)      // arArIntNums: jogo preparado para ser jogado
                //-----------------------

                Log.d(cTAG, "-> Jogo preparado:")

                //------------------------------------
                listaQM(arArIntNums, false)
                //------------------------------------

                // sudoku_#9.0.172
                //--- String para uso no checador de UNIQUE do site https://www.sudoku-solutions.com/
                strLog = ""
                for (x in 0..8) {
                    for (y in 0..8) {
                        strLog += arArIntNums[x][y].toString()
                    }
                }
                Log.d(cTAG, strLog)

                val intQtiZeros = utilsKt.quantZeros(arArIntNums)
                val intQtiClues = 81 - intQtiZeros

                val strQtiZerosPad = intQtiZeros.toString().padStart(4)
                strLog = String.format("%s %s", "-> Quantidade de clues: ", "$intQtiClues")
                Log.d(cTAG, strLog)
                txtDados = "${txtDados}\n$strLog"

                //------------------------------------------------------------------
                //               Gera uma solução para o jogo preparado
                //------------------------------------------------------------------
                //--- Atribui um nível ao jogo: resolve o jogo pelo algoritmo backTracking;
                //    considerarei como o "nível" do jogo, quantas vezes foi necessária a recursão.
                //---------------------------------------------

                intNumBackTracking = 0
                val arArIntCopia = copiaArArInt(arArIntNums)
                //================================================================================
                val flagSolOk = SudokuBackTracking.solveSudoku(arArIntCopia, arArIntCopia.size)
                //================================================================================
                if (flagSolOk) {

                    Log.d(cTAG, "-> Gabarito gerado pelo Backtracking:")
                    //-------------------------------------
                    listaQM(arArIntCopia, false)
                    //-------------------------------------

                    //--- Verifica se Solução UNIQUE
                    //if (arArIntCopia != quadMaiorRet) {
                    var flagErro = false
                    for (intLin in 0..8) {

                        for (intCol in 0..8) {

                            if (arArIntCopia[intLin][intCol] != quadMaiorRet[intLin][intCol]) {

                                Log.d(cTAG, "-> Solução NÃO UNIQUE!")
                                flagErro = true
                                break

                            }

                            if (flagErro) break

                        }
                    }

                    if (!flagErro) {

                        Log.d(cTAG, "-> Solução UNIQUE!")

                        /*
                        val strNivelJogoPad = intNumBackTracking.toString().padStart(4)
                        strLog   = String.format( "%s %s", "-> Nível do jogo adaptado:", strNivelJogoPad)
                         */

                        val intNivelJogo    = intQtiZeros / 10
                        val intSubnivelJogo = intQtiZeros % 10
                        val strNivelJogo    = when (intNivelJogo) {

                            2 -> "Fácil"
                            3 -> "Médio"
                            4 -> "Difícil"
                            5 -> "Muito difícil"
                            else -> "Fácil"

                        }

                        strLog      = "-> Nível jogo gerado: $strNivelJogo"
                        Log.d(cTAG, strLog)
                        val strLog1 = "    - subnível: $intSubnivelJogo"
                        Log.d(cTAG, strLog1)

                        txtDados = "${txtDados}\n$strLog\n$strLog1"

                        flagJogoVal = true

                    }
                }
            }

            if (flagJogoVal) {

                Log.d(cTAG, "-> Solução UNIQUE!")
                sggFlagJogoGeradoOk = true

            }
            //--- Jogo NÃO válido, se ainda não chegou ao limite tenta gerar outro
            else {

                contaTentaJogo++
                txtDados = "${txtDados}\n"

            }

        }

        //--- Não conseguiu gerar um jogo
        if (contaTentaJogo >= limTentaJogo) {

            Log.d(cTAG, "-> Tentou $limTentaJogo jogos. Fim.")
            arArIntNums = Array(9) { Array(9) {0} }

        }

        return arArIntNums   // Jogo preparado
        
    }

    //--- geraQuadMenor[quad]
    private fun geraQuadMenor (quadMenor: Int) : Array <Int> {

        //--- Instancializações e inicializações
        val arQuadMenor = arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)
        val numDispCel  = arrayOf (0, 0, 0, 0, 0, 0, 0, 0, 0)

        //--- Para todas as linhas do Qm
        for (linQm in 0..2) {

            //--- Para todas as colunas de Qm
            for (colQm in 0..2) {

                val fimTenta          = 20   // 50  // 10
                var contaTentaGerarQm = 0
                var flagNumOk         = false
                var numero : Int
                while (!flagNumOk && contaTentaGerarQm < fimTenta ) {

                    //--- Gera número aleatório sem repetição
                    //-------------------------
                    numero = (1..9).random()
                    //-------------------------

                    //secureTrnd.setSeed(secureTrnd.generateSeed(16))  // 32  64 128 NÃO OK
                    //numero = secureTrnd.nextInt(9) + 1

                    // Critério1: sem repetição no próprio Qm
                    if (numDispCel[numero - 1] == 0) {

                        arQuadMenor[linQm * 3 + colQm] = numero
                        flagNumOk = true

                    }

                    if (flagNumOk) {

                        // Critério2: verifica se número gerado pode ser inserido no jogo
                        //-----------------------------------------------------------
                        flagNumOk = verifValidade(quadMenor, linQm, colQm, numero)
                        //-----------------------------------------------------------

                    }
                    if (!flagNumOk) {

                        //--- Se o número gerado NÃO está ok (está presente na mesma linha ou coluna de
                        //    outro bloco) armazena -1 para sinalizar erro.
                        if (++contaTentaGerarQm >= fimTenta) {

                            arQuadMenor[linQm * 3 + colQm] = -1

                        }

                    }

                    //--- Se o número gerado ESTÁ ok armazena no array desse bloco
                    else {

                        arQuadMenor[linQm * 3 + colQm] = numero
                        numDispCel[numero - 1] = numero

                    }

                }
            }
        }

        return arQuadMenor

    }

    //--- inicquadMaiorRetGeracao
    private fun zeraQuadMaiorGeracao() {

        quadMaiorRet = arrayOf()

        for (linha in 0..8) {

            var array = arrayOf<Int>()

            for (coluna in 0..8) { array += 0 }

            quadMaiorRet += array

        }
    }

    //--------------------------------------------------------------------------
    //                      A partir de presets gera jogo
    //--------------------------------------------------------------------------
    //--- AdaptaJogo algoritmo 2
    @SuppressLint("SetTextI18n")
    fun adaptaJogoAlgoritmo2 () : Array<Array<Int>> {

        val limAdaptaJogo   = 1    //20
        var contaAdaptaJogo = 0
        var arArIntJogo     = Array(9) { Array(9) {0} }

        sggFlagJogoAdaptadoOk  = false

        while (++contaAdaptaJogo <= limAdaptaJogo) {

            Log.d(cTAG, "-> Preset $intJogoAdaptar")
            Log.d(cTAG, "   - tenta adaptar o jogo ($contaAdaptaJogo)")

            //val strTmp = "\n-> Adapta jogo com preset $intJogoAdaptar"

            // txtDados = "${txtDados}$strTmp"
            txtDados = ""

            //---------------------------------------
            //inicQuadMaiorAdaptacao(intJogoAdaptar)
            //---------------------------------------

            //--- Preset enviado pelo MainActivity
            //-------------------------------------
            listaQM(quadMaiorRet, false)
            //-------------------------------------
            arArIntJogo = copiaArArInt(quadMaiorRet)

            intNumBackTracking = 0
            //-------------------------------------------------------------------------------------
            sggFlagJogoAdaptadoOk = SudokuBackTracking.solveSudoku(quadMaiorRet, quadMaiorRet.size)
            //-------------------------------------------------------------------------------------
            //val intNumBackTracking = SudokuBackTracking.intNumBackTracking
            if (sggFlagJogoAdaptadoOk) {

                strLog  = "-> Jogo com preset $intJogoAdaptar adaptado"
                Log.d(cTAG, strLog)

                txtDados = strLog

                Log.d(cTAG, "-> Gabarito do jogo (adaptação do preset):")
                //txtDados = "${txtDados}\n-> Gabarito:"
                //-------------------------------------
                listaQM(quadMaiorRet, false)
                //-------------------------------------

                Log.d(cTAG, "-> Jogo (preset):")
                //txtDados = "${txtDados}\n-> Preset:"
                //-------------------------------------
                listaQM(arArIntJogo, true)
                //-------------------------------------

                intQtiZeros = utilsKt.quantZeros(arArIntJogo)

                val strQtiZerosPad = intQtiZeros.toString().padStart(4)
                strLog   = String.format ( "%s %s", "-> Quantidade de clues:", strQtiZerosPad)
                Log.d(cTAG, strLog)
                txtDados = "${txtDados}\n$strLog"

                /*
                val strNivelJogoPad = intNumBackTracking.toString().padStart(4)
                strLog   = String.format( "%s %s", "-> Nível do jogo adaptado:", strNivelJogoPad)
                Log.d(cTAG, strLog)
                txtDados = "${txtDados}\n$strLog"
                 */

                val intNivelJogo    = intQtiZeros / 10
                val intSubnivelJogo = intQtiZeros % 10
                val strNivelJogo = when (intNivelJogo) {

                    2 -> "Fácil"
                    3 -> "Médio"
                    4 -> "Difícil"
                    5 -> "Muito difícil"
                    else -> "Fácil"

                }

                strLog  = "-> Nível jogo adaptado: $strNivelJogo"
                Log.d(cTAG, strLog)
                val strLog1 = "    - subnível: $intSubnivelJogo"
                Log.d(cTAG, strLog1)

                txtDados = "${txtDados}\n$strLog\n$strLog1"

            }
            else {

                strLog = "-> Jogo com preset $intJogoAdaptar NÃO adaptado!"
                Log.d(cTAG, strLog)

            }
        }

        return arArIntJogo

    }

    //----------------------------------------------------------------------------------------------
    // Funções gerais
    //----------------------------------------------------------------------------------------------
    //--- Calcula as linhas do QM para um Qm
    private fun calcLinsQM (quadMenor : Int) : Array <Int> {

        // INT(EXT.TEXTO(F17;2;1)/3)*3
        val linInicQM = (quadMenor / 3) * 3

        return (arrayOf(linInicQM, linInicQM + 1, linInicQM + 2))

    }

    //--- Calcula as colunas do QM para um Qm
    private fun calcColsQM(quadMenor: Int): Array<Int> {

        // EXT.TEXTO(F17;2;1)*3-INT(EXT.TEXTO(F17;2;1)/3)*9
        val colInicQM = quadMenor * 3 - (quadMenor / 3) * 9

        return (arrayOf(colInicQM, colInicQM + 1, colInicQM + 2))

    }

    //--- insere Qm no QM
    // Retorna na global 'quadMaiorRet'
    private fun insereQmEmQM (quadMenor : Int, array : Array <Int>) {

        // Converte os quadrados menores no quadMaiorRet
        var valCel : Int

        //--- Calcula as linhas desse quadrado
        //------------------------------------------
        val linhasQuadQM = calcLinsQM (quadMenor)
        //------------------------------------------
        //--- Calcula as colunas desse quadrado
        //----------------------------------------
        val colsQuadQM = calcColsQM (quadMenor)
        //----------------------------------------

        for (linMenor in 0..2) {

            for (colMenor in 0..2) {

                valCel = array[linMenor * 3 + colMenor]

                quadMaiorRet[linhasQuadQM[linMenor]][colsQuadQM[colMenor]] = valCel

            }
        }
    }

    //--- listaquadMaiorRet
    // flagShow: se true libera a apresentação do QM na tela do smartphone
    @SuppressLint("SetTextI18n")
    fun listaQM (quadMaior: Array<Array<Int>>, flagShow : Boolean) {

        var strDados: String
        for (linha in 0..8) {

            strLog   = "linha $linha : "
            strDados = ""
            for (coluna in 0..8) {

                val strTmp = "${quadMaior[linha][coluna]}" + if (coluna < 8) ", " else ""
                strDados += strTmp
                strLog   += strTmp

            }
            Log.d(cTAG, strLog)

            val strDadosTmp = "\n" + strDados
            //val strDadosTmp = strDados

            if (flagShow) txtDados = "${txtDados}$strDadosTmp"

        }
    }

    //--- verifValidade de um número do Qm para inserção no QM
    private fun verifValidade(quadMenor : Int, linhaQm : Int, colunaQm : Int,
                              numero : Int) : Boolean {
        var flagNumeroOk = true

        //--- Calcula as linhas desse quadrado menor no QM
        //--------------------------------------
        val linhasQM = calcLinsQM (quadMenor)
        //--------------------------------------
        //--- Calcula as colunas desse quadrado menor no QM
        //------------------------------------
        val colsQM = calcColsQM (quadMenor)
        //------------------------------------

        //--- Converte a linha do numero gerado do Qm para a do QM
        val linQM = linhasQM[linhaQm]
        //--- Converte a coluna do numero gerado do Qm para a do QM
        val colQM = colsQM[colunaQm]

        //--- Verifica se número existe na LINHA do QM
        var numeroQM : Int
        for (idxColQM in 0..8) {

            if (!colsQM.contains(idxColQM)) {

                numeroQM = quadMaiorRet[linQM][idxColQM]
                if (numero == numeroQM) {

                    flagNumeroOk = false
                    break

                }
            }
        }

        //--- Se existe, retorna para gerar um novo numero
        if (!flagNumeroOk) return false

        //--- Se não existe na linha, verifica se número existe na COLUNA do QM
        else {

            for (idxLinQM in 0..8) {

                if (!linhasQM.contains(idxLinQM)) {

                    numeroQM = quadMaiorRet[idxLinQM][colQM]
                    if (numero == numeroQM) {

                        flagNumeroOk = false
                        break

                    }
                }
            }
        }

        return flagNumeroOk

    }

    //--- copiaArArInt
    private fun copiaArArInt(arArIntPreset: Array<Array<Int>>): Array<Array<Int>> {

        /* https://stackoverflow.com/questions/45199704/kotlin-2d-array-initialization
            // A 6x5 array of Int, all set to 0.
            var m = Array(6) {Array(5) {0} }
         */

        //-------------------------------------------------------
        val arArIntTmp = Array (9) {Array (9) { 0 } }
        //-------------------------------------------------------

        for (intLin in 0..8) {
            for (intCol in 0..8) {
                arArIntTmp  [intLin][intCol] = arArIntPreset[intLin][intCol]
            }
        }

        return arArIntTmp

    }

    //--- Prepara o jogo gerado (arArIntNums[9][9]) conforme as Regras 1,2,3,4
    private fun preparaJogo(nivelJogo : Int) {

        //Log.d(cTAG, "-> Jogo antes da preparação:")
        //-----------------------
        //listaQM (arArIntNums)
        //-----------------------

        //------------------------------------------------------------------------------------------
        // Regra1: todos os Qm devem conter pelo menos dois zeros cada
        //------------------------------------------------------------------------------------------
        var qtiZerosNivel = 0
        for (quadMenor in 0..8) {

            //--- Determina as coordenadas (3x3) do QM para esse Qm
            val arLinsQM = calcLinsQM(quadMenor)
            val arColsQM = calcColsQM(quadMenor)

            //--- Preenche esse Qm com os números gerados (QM)
            val arIntCelQM = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            for (idxLin in 0..2) {
                for (idxCol in 0..2) {

                    arIntCelQM[idxLin*3 + idxCol] = arArIntNums[arLinsQM[idxLin]][arColsQM[idxCol]]

                }
            }

            //--- Aleatóriamente preenche 2 células do Qm com zeros
            var intQtiZeros = 0
            val arIntNumRnd = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

            var flagQmOk    = false
            while (!flagQmOk) {

                var flagNumOk = false
                while (!flagNumOk) {

                    //--- Gera número aleatório sem repetição
                    //-----------------------------
                    val numRnd = (1..9).random()
                    //-----------------------------
                    if (arIntNumRnd[numRnd - 1] > 0) {

                        arIntNumRnd[numRnd - 1] = 0
                        flagNumOk = true

                        //--- Insere clues, se inseriu dois zeros passa ao px Qm
                        arIntCelQM [numRnd - 1] = 0
                        if (++intQtiZeros > 1) flagQmOk = true

                    }
                }
            }

            //--- Transfere os valores gerados do Qm ao QM (jogo a ser jogado)
            for (linMenor in 0..2) {

                for (colMenor in 0..2) {

                    val valCel = arIntCelQM[linMenor * 3 + colMenor]
                    arArIntNums[arLinsQM[linMenor]][arColsQM[colMenor]] = valCel

                    //--- Computa os zeros inseridos para definição do nível Fácil
                    if (valCel == 0 && (++qtiZerosNivel >= nivelJogo)) break

                }
            }
        }
        //Log.d(cTAG, "-> Jogo após a preparação conforme a R1 (Qm's):")
        //-------------------------------------
        //listaQM (arArIntNums, false)
        //-------------------------------------
        //Log.d(cTAG, "   - R1: qti clues = $qtiZerosNivel")

        //------------------------------------------------------------------------------------------
        // Regra2: cada linha devem conter pelo menos dois zeros
        //------------------------------------------------------------------------------------------
        for (intLinha in 0..8) {

            val arIntNumRnd = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

            //--- Lê a linha toda no QM e armazena-a num vetor
            val arIntCelLin = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            for (intCol in 0..8) {arIntCelLin[intCol] = arArIntNums[intLinha][intCol]}

            //--- Conta qtos zeros essa linha já tem
            var intQtiZeros = 0
            for (intCol in 0..8) { if (arIntCelLin[intCol] == 0) intQtiZeros++ }

            //--- Enqto NÃO tiver pelo menos 2 zeros na linha, gera um índice aleatório e se o vetor
            //    que controla os numRND tiver nesse índice valor diferente de zero, zera-o.
            while (intQtiZeros < 2 && qtiZerosNivel < nivelJogo) {

                var flagNumOk = false
                while (!flagNumOk) {

                    //--- Gera número aleatório sem repetição
                    //-----------------------------
                    val numRnd = (1..9).random()
                    //-----------------------------
                    if (arIntNumRnd[numRnd - 1] > 0) {

                        arIntNumRnd[numRnd - 1] = 0
                        if (arIntCelLin [numRnd - 1] > 0) {

                            arIntCelLin [numRnd - 1] = 0
                            qtiZerosNivel ++

                        }

                        flagNumOk = true
                        intQtiZeros ++

                    }
                }
            }
            //--- Retorna as células à linha
            for (idxColQM in 0..8) { arArIntNums[intLinha][idxColQM] = arIntCelLin[idxColQM] }
        }
        //Log.d(cTAG, "-> Jogo após a preparação conforme R2 (lin):")
        //-------------------------------------
        //listaQM (arArIntNums, false)
        //-------------------------------------
        //Log.d(cTAG, "   - R2: qti clues = $qtiZerosNivel")

        //------------------------------------------------------------------------------------------
        // Regra3: todas as colunas devem conter pelo menos dois zeros
        //------------------------------------------------------------------------------------------
        for (intCol in 0..8) {

            //--- Vetor para evitar o uso de idx repetidos
            val arIntNumRnd = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

            //--- Lê a coluna toda e armazena-a num vetor
            val arIntCelCol = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            for (intLinha in 0..8) {arIntCelCol[intLinha] = arArIntNums[intLinha][intCol]}

            //--- Conta qtos zeros essa linha já tem
            var intQtiZeros = 0
            for (intLinha in 0..8) { if (arIntCelCol[intLinha] == 0) intQtiZeros++ }

            //--- Enqto NÃO tiver pelo menos 2 zeros na linha, gera um índice aleatório e se o vetor
            //    que controla os numRND tiver nesse índice valor diferente de zero, zera-o.
            while (intQtiZeros < 2 && qtiZerosNivel < nivelJogo) {

                var flagNumOk = false
                while (!flagNumOk) {

                    //--- Gera número aleatório sem repetição
                    //-----------------------------
                    val numRnd = (1..9).random()
                    //-----------------------------
                    if (arIntNumRnd[numRnd - 1] > 0) {

                        arIntNumRnd[numRnd - 1] = 0
                        if (arIntCelCol [numRnd - 1] > 0) {

                            arIntCelCol [numRnd - 1] = 0
                            qtiZerosNivel++

                        }

                        flagNumOk = true
                        intQtiZeros ++

                    }
                }
            }
            //--- Retorna as células à coluna
            for (idxLinhaQM in 0..8) { arArIntNums[idxLinhaQM][intCol] = arIntCelCol[idxLinhaQM] }

        }
        //Log.d(cTAG, "-> Jogo após a preparação conforme R3 (col):")
        //-------------------------------------
        //listaQM (arArIntNums, false)
        //-------------------------------------
        //Log.d(cTAG, "   - R3: qti clues = $qtiZerosNivel")

        //------------------------------------------------------------------------------------------
        // Regra4: para os níveis médios completa as casas com zero conforme o nível do jogo
        //------------------------------------------------------------------------------------------

        //--- Para a Regra4, determina a qtidd de Zeros no jogo
        //------------------------------------------
        var intQtiZeros = utilsKt.quantZeros(arArIntNums)
        //------------------------------------------
        //--- Inicializa um vetor para evitar repetição de números Rnd
        val arIntNumRnd = Array(81) { 0 }
        for (idxConta in 0..80) { arIntNumRnd[idxConta] = idxConta + 1 }

        while (intQtiZeros < nivelJogo) {

            var flagNumOk = false
            while (!flagNumOk) {

                //--- Gera número aleatório sem repetição
                //---------------------------------
                val numRndGen =
                    (1..81).random()     // Gera os números aleatórios de 1 a 81 inclus.
                //---------------------------------

                //Log.d(cTAG, "-> numRnd = $numRnd arIntNumRnd[numRnd]=${arIntNumRnd[numRnd]}" )
                val numRnd = numRndGen - 1
                if (arIntNumRnd[numRnd] > 0) {

                    arIntNumRnd[numRnd] = 0

                    val intLinha = numRnd / 9
                    val intColuna = numRnd % 9
                    if (arArIntNums[intLinha][intColuna] > 0) {

                        arArIntNums[intLinha][intColuna] = 0
                        intQtiZeros++

                    }
                    //Log.d(cTAG, "-> linha = $intLinha coluna = $intColuna" )

                    flagNumOk = true

                }
            }
        }
        //strLog   = "-> Jogo após a preparação conforme R4 (QM):"
        //Log.d(cTAG, strLog)
        //------------------------------------
        //listaQM(arArIntNums, false)
        //------------------------------------
        //Log.d(cTAG, "   - R4: qti clues = $intQtiZeros")

        txtDados = "-> Jogo preparado R(1,2,3,4):"

    }

    //--- geraJogoAlgScott()
    private fun geraJogoAlgScott() : Array<Array<Int>> {

        val arArIntJogoScott = Array(9) { Array(9) {0} }

        //--- Instancializações e inicializações
        var k1: Int
        var k2: Int
        var counter = 1

        //----------------------------------------------------------------------
        // 1- Gera o tabuleiro
        //----------------------------------------------------------------------
        val board = SudokuBoard()

        //==================
        board.generate()
        //==================
        var myBoard = board.board

        //--- Apresenta o tabuleiro gerado
        Log.d(cTAG, "-> Original board:")
        for (i in 0..8) {
            strLog = ""
            for (j in 0..8) { strLog += "${myBoard[i][j]}   " }
            Log.d(cTAG, strLog)
        }

        //----------------------------------------------------------------------
        // 2- Swaps
        //----------------------------------------------------------------------
        // Swap singles
        SudokuBoard.gen_rand(1) // rows
        SudokuBoard.gen_rand(0) // cols

        // Swap groups
        val rand = Random()
        val n = intArrayOf(0, 3, 6)
        for (i in 0..1) {
            k1 = n[rand.nextInt(n.size)]
            do {
                k2 = n[rand.nextInt(n.size)]
            } while (k1 == k2)
            if (counter == 1) SudokuBoard.swap_row_group(k1, k2) else SudokuBoard.swap_col_group(
                k1,
                k2
            )
            counter++
        }

        //--- Apresenta o tabuleiro após swaps
        Log.d(cTAG, "-> Swapped board:")
        myBoard = board.board
        for (x in 0..8) {
            strLog = ""
            for (y in 0..8) { strLog += "${myBoard[x][y]}   " }
            Log.d(cTAG, strLog)
        }

        //----------------------------------------------------------------------
        // 3- Striking out
        //----------------------------------------------------------------------
        k1 = 0
        while (k1 < 9) {
            k2 = 0
            while (k2 < 9) {
                //-------------------------------
                SudokuBoard.strike_out(k1, k2)
                //-------------------------------
                k2++
            }
            k1++
        }

        //--- Apresenta o tabuleiro final
        Log.d(cTAG, "Final Board:")
        for (i in 0..8) {
            strLog = ""
            for (j in 0..8) { strLog += "${myBoard[i][j]}   " }
            Log.d(cTAG, strLog)
        }

        //---------------------------------------------------------------------------------
        // 4- Converte o tipo de dados de Array<(out) IntArray!>! para Array<Array<Int>>
        //---------------------------------------------------------------------------------
        for (x in 0..8) {
            for (y in 0..8) { arArIntJogoScott[x][y] = "${myBoard[x][y]}".toInt() }
        }
        val qtizeros = utilsKt.quantZeros(arArIntJogoScott)
        Log.d(cTAG, "-> células vazias: $qtizeros   clues: ${81-qtizeros}")

        return arArIntJogoScott

    }

}