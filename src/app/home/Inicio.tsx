import { Button, FlatList, Modal, Pressable, ScrollView, StyleProp, StyleSheet, Text, TextStyle, TouchableNativeFeedback, View, ViewStyle } from "react-native";
import Colors from "../../ui/Colors";
import { BottomTabScreenProps } from "@react-navigation/bottom-tabs";
import NunitoText from "../../shared/NunitoText";
import MyFinancesIcon from "../../ui/icons/MyFinancesIcon";
import { Children, PropsWithChildren, ReactNode, useState } from "react";
import SetaUmSvg from "../../ui/svg/SetaUmSvg";
import OlhoAbertoSvg from "../../ui/svg/OlhoAbertoSvg";
import OlhoFechadoSvg from "../../ui/svg/OlhoFechadoSvg";
import DadosSensiveisText from "../../shared/DadosSensiveisText";
import AlimentacaoIcon from "../../ui/icons/AlimentacaoIcon";
import SetaDoisSvg from "../../ui/svg/SetaDoisSvg";
import { CategoriaGastoEnum } from "../../constantes/CategoriaGastoEnum";
import InvestimentoIcon from "../../ui/icons/InvestimentoIcon";
import PiramideIcon from "../../ui/icons/PiramideIcon";
import MoedaIcon from "../../ui/icons/MoedaIcon";
import MedicacaoIcon from "../../ui/icons/MedicacaoIcon";
import { mask } from "react-native-mask-text";
import { maskBRL, maskMesAnoAbreviado } from "../../utils/maskUtils";
import Gasto from "../../shared/Gasto";
import { nomeMesArray } from "../../constantes/MesesConstante";
import NativeButton from "../../shared/NativeButton";
import XSvg from "../../ui/svg/XSvg";

const oColors: any = Colors;

interface dadoMes {
    mes: number,
    ano: number,
    gastoTotal: number
}


export default function InicioScreen({ navigation, route }: BottomTabScreenProps<any>) {

    const [mesSelecionado, setMesSelecionado] = useState({mes: 1, ano: 2024})
    const [mostrarValores, setMostrarValores] = useState(true)
    const [modalIsVisible, setModalIsVisible] = useState(false)

    let dadosUltimos6Meses = [
        { ano: 2023, mes: 10, gastoTotal: 400 },
        { ano: 2023, mes: 11, gastoTotal: 1550.8 },
        { ano: 2023, mes: 12, gastoTotal: 756.3 },
        { ano: 2024, mes: 1, gastoTotal: 220.89 },
        { ano: 2024, mes: 2, gastoTotal: 500.85 },
        { ano: 2024, mes: 3, gastoTotal: 800.65 }
    ]

    let mesesParaSelecionar = [
        { ano: 2023, mes: 10 },
        { ano: 2023, mes: 11 },
        { ano: 2023, mes: 12 },
        { ano: 2024, mes: 1 },
        { ano: 2024, mes: 2 },
        { ano: 2024, mes: 3 }
    ]

    return (
        <>
            <Button title="dev helper" onPress={() => { navigation.navigate("devHelpReact") }} />
            <ScrollView style={[styles.container]}>
                <View style={[styles.topPage]}>
                    <MyFinancesIcon height="40" width="40"
                        firstColor={Colors.verde[500]} secondColor={Colors.amarelo[500]} threeColor={Colors.vermelho[600]} />
                        
                    <NativeButton 
                    onPress={() => setModalIsVisible(true)} 
                    feedbackColor={Colors.cinza[500]}
                    styleView={styles.buttonSelectMes}
                    styleWrapper={{borderRadius: 50}} >
                        <NunitoText style={[{ color: Colors.cinza[800], fontSize: 15, marginRight: 8 }]}>
                            {maskMesAnoAbreviado(mesSelecionado.mes, mesSelecionado.ano)}
                        </NunitoText>
                        <SetaUmSvg color={Colors.cinza[800]} height="10" width="16" />
                    </NativeButton>
                </View>
                <Section marginBottom={20} backgroundColor={Colors.vermelho[50]}>
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "center",
                        marginBottom: 20
                    }}>
                        <View style={{
                            alignItems: "center"
                        }} >
                            <NunitoText style={{ color: Colors.cinza[950] }}>Saldo em contas</NunitoText>
                            <DadosSensiveisText style={{
                                fontSize: 23,
                                color: Colors.cinza[950]
                            }} isVisible={mostrarValores} >{maskBRL(526.32)}</DadosSensiveisText>
                        </View>
                    </View>
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "space-evenly",
                        marginBottom: 20
                    }}>
                        <View style={{ alignItems: "center", flex: 2 }}>
                            <NunitoText style={{ color: Colors.verde[600] }}>Entrada</NunitoText>
                            <DadosSensiveisText style={{
                                fontSize: 20,
                                color: Colors.verde[950]
                            }} isVisible={mostrarValores}>{maskBRL(1230)}</DadosSensiveisText>
                        </View>
                        <View style={{ alignItems: "center", flex: 1 }}>
                            <Pressable style={{
                                width: 30,
                                height: 30,
                                alignItems: "center",
                                justifyContent: "center",
                                transform: [{ translateY: -15 }]
                            }} onPress={() => {
                                setMostrarValores(!mostrarValores)
                            }}>
                                {
                                    mostrarValores ?
                                        <OlhoAbertoSvg height="24" width="36" colorPrimary={Colors.cinza[800]} colorSecondary={Colors.vermelho[50]} /> :
                                        <OlhoFechadoSvg height="24" width="36" color={Colors.cinza[800]} />
                                }

                            </Pressable>
                        </View>
                        <View style={{ alignItems: "center", flex: 2 }}>
                            <NunitoText style={{ color: Colors.vermelho[600] }}>Saída</NunitoText>
                            <DadosSensiveisText isVisible={mostrarValores} style={{
                                fontSize: 20,
                                color: Colors.vermelho[950]
                            }}>{maskBRL(703.68)}</DadosSensiveisText>
                        </View>
                    </View>
                </Section>
                <Section marginBottom={20} backgroundColor={Colors.verde[50]} >
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "space-between",
                        marginBottom: 20
                    }}>
                        <View style={{ flex: 1 }}>
                            <AlimentacaoIcon height="35" width="35" firstColor={Colors.verde[950]} secondColor={Colors.verde[50]} thirdColor={Colors.verde[50]} />
                        </View>
                        <View style={{
                            alignItems: "center",
                            flex: 3
                        }} >
                            <NunitoText style={{ color: Colors.cinza[950], fontSize: 10 }}>Saldo vales alimentação</NunitoText>
                            <DadosSensiveisText style={{
                                fontSize: 23,
                                color: Colors.cinza[950]
                            }} isVisible={mostrarValores} >{maskBRL(1800 - 1453.68)}</DadosSensiveisText>
                        </View>
                        <Pressable style={{ flex: 1, alignItems: "flex-end" }}>
                            <SetaDoisSvg height="20" width="18" color={Colors.cinza[800]} />
                        </Pressable>
                    </View>
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "space-between",
                        marginBottom: 20
                    }}>
                        <View style={{ alignItems: "center", flex: 2 }}>
                            <NunitoText style={{ color: Colors.verde[600] }}>Entrada</NunitoText>
                            <DadosSensiveisText style={{
                                fontSize: 20,
                                color: Colors.verde[950]
                            }} isVisible={mostrarValores}>{maskBRL(1800)}</DadosSensiveisText>
                        </View>
                        <View style={{ alignItems: "center", flex: 2 }}>
                            <NunitoText style={{ color: Colors.vermelho[600] }}>Saída</NunitoText>
                            <DadosSensiveisText isVisible={mostrarValores} style={{
                                fontSize: 20,
                                color: Colors.vermelho[950]
                            }}>R$ 1453,68</DadosSensiveisText>
                        </View>
                    </View>
                </Section>
                <Section marginBottom={20} backgroundColor={Colors.vermelho[50]}>
                    <View style={{
                        flexDirection: "row",
                        justifyContent: "space-between",
                        marginBottom: 10
                    }}>
                        <NunitoText style={{ color: Colors.cinza[800], fontSize: 15 }}>Falta pagar</NunitoText>
                        <Pressable>
                            <SetaDoisSvg height="20" width="18" color={Colors.cinza[800]} />
                        </Pressable>
                    </View>
                    <View style={{
                        minHeight: 60,
                        flexDirection: "row",
                        flexWrap: "wrap"
                    }}>
                        <Gasto styleView={{ width: 140, marginRight: 5, marginBottom: 10 }} isVisible={mostrarValores} title="Caixa Emergencial" sizeIcon={45} valor={151.65} categoria={CategoriaGastoEnum.INVESTIMENTO} />
                        <Gasto styleView={{ width: 140, marginRight: 5, marginBottom: 10 }} isVisible={mostrarValores} title="Pão de queijo" sizeIcon={45} valor={6} categoria={CategoriaGastoEnum.ALIMENTACAO} />
                        <Gasto styleView={{ width: 140, marginRight: 5, marginBottom: 10 }} isVisible={mostrarValores} title="Livro" sizeIcon={45} valor={4.97} categoria={CategoriaGastoEnum.EXTRAS} />
                    </View>
                </Section>
                <Section marginBottom={20} backgroundColor={Colors.vermelho[50]}>
                    <NunitoText style={{ color: Colors.cinza[800], fontSize: 15 }}>Últimos gastos</NunitoText>
                    <View style={{
                        marginVertical: 5,
                        borderBottomColor: Colors.cinza[300],
                        borderBottomWidth: 2,
                        flexDirection: "row",
                        justifyContent: "space-between",
                        alignItems: "center"
                    }}>
                        <Gasto styleView={{
                            marginBottom: 7
                        }} isVisible={mostrarValores} sizeIcon={45} title="Pão de queijo" valor={6} categoria={CategoriaGastoEnum.ALIMENTACAO} />
                        <NunitoText style={{ color: Colors.cinza[800], fontSize: 15 }}>24/03</NunitoText>
                    </View>
                    <View style={{
                        marginVertical: 5,
                        borderBottomColor: Colors.cinza[300],
                        borderBottomWidth: 2,
                        flexDirection: "row",
                        justifyContent: "space-between",
                        alignItems: "center"
                    }}>
                        <Gasto styleView={{
                            marginBottom: 7
                        }} isVisible={mostrarValores} sizeIcon={45} title="Livro" valor={46.65} categoria={CategoriaGastoEnum.EXTRAS} />
                        <NunitoText style={{ color: Colors.cinza[800], fontSize: 15 }}>24/03</NunitoText>
                    </View>
                    <View style={{
                        marginVertical: 5,
                        borderBottomColor: Colors.cinza[300],
                        borderBottomWidth: 2,
                        flexDirection: "row",
                        justifyContent: "space-between",
                        alignItems: "center"
                    }}>
                        <Gasto styleView={{
                            marginBottom: 7
                        }} isVisible={mostrarValores} sizeIcon={45} title="Pão de queijo" valor={6} categoria={CategoriaGastoEnum.ALIMENTACAO} />
                        <NunitoText style={{ color: Colors.cinza[800], fontSize: 15 }}>24/03</NunitoText>
                    </View>
                </Section>
                <Section marginBottom={20} backgroundColor={Colors.vermelho[50]}>
                    <NunitoText style={{ color: Colors.cinza[800], fontSize: 15 }}>Despesas por categoria</NunitoText>
                    <View style={{
                        flexDirection: "row",
                        flexWrap: "wrap",
                    }}>
                        <View style={{
                            flexDirection: "row",
                            alignItems: "flex-end",
                            marginBottom: 10,
                            marginRight: 10
                        }}>
                            <Gasto isVisible={mostrarValores} sizeIcon={45} title="Investimentos" valor={100} categoria={CategoriaGastoEnum.INVESTIMENTO} />
                            <NunitoText style={{ color: Colors.cinza[800], fontSize: 12 }}>{20}%</NunitoText>
                        </View>
                        <View style={{
                            flexDirection: "row",
                            alignItems: "flex-end",
                            marginBottom: 10,
                            marginRight: 2.5
                        }}>
                            <Gasto isVisible={mostrarValores} sizeIcon={45} title="Extras" valor={100} categoria={CategoriaGastoEnum.EXTRAS} />
                            <NunitoText style={{ color: Colors.cinza[800], fontSize: 12 }}>{20}%</NunitoText>
                        </View>
                        <View style={{
                            flexDirection: "row",
                            alignItems: "flex-end",
                            marginBottom: 10,
                            marginRight: 10
                        }}>
                            <Gasto isVisible={mostrarValores} sizeIcon={45} title="Alimentação" valor={100} categoria={CategoriaGastoEnum.ALIMENTACAO} />
                            <NunitoText style={{ color: Colors.cinza[800], fontSize: 12 }}>{20}%</NunitoText>
                        </View>
                    </View>
                </Section>
                <Section marginBottom={20} backgroundColor={Colors.vermelho[50]}>
                    <NunitoText style={{ color: Colors.cinza[800], fontSize: 15 }}>Cartões</NunitoText>
                    <NunitoText style={{ color: Colors.cinza[800], fontSize: 15, alignSelf: "center" }}>Em breve...</NunitoText>
                </Section>
                <Section marginBottom={20} backgroundColor={Colors.vermelho[50]}>
                    <NunitoText style={{ color: Colors.cinza[800], fontSize: 15 }}>Gastos últimos 6 meses</NunitoText>
                    <GraficoGastoMes valoresVisiveis={mostrarValores} dadosMeses={dadosUltimos6Meses} />
                </Section>
            </ScrollView>
            <Modal animationType="fade" visible={modalIsVisible} onRequestClose={() => setModalIsVisible(false)} transparent={true}>
                <View style={{
                    backgroundColor: Colors.cinza[900]+"80",
                    width: '100%',
                    height: '100%',
                    justifyContent: "center",
                    alignItems: "center"
                }}>
                    <View style={{
                        backgroundColor: Colors.vermelho[50],
                        paddingHorizontal: 20,
                        paddingVertical: 10,
                        borderRadius: 10,
                    }}>
                        <View style={{
                            flexDirection: "row",
                            justifyContent: "space-between",
                            alignItems: "center"
                        }}>
                            <NunitoText style={{color: Colors.cinza[950], fontSize: 15}}>Meses:</NunitoText>
                            <NativeButton styleView={{paddingVertical:10, paddingLeft:40}} onPress={()=>setModalIsVisible(false)} feedbackColor={Colors.cinza[500]} >
                                <XSvg color={Colors.cinza[950]} height="15" width="15"/>
                            </NativeButton>
                        </View>
                        <View style={{
                            borderColor: Colors.cinza[950],
                            borderWidth: 1,
                            paddingHorizontal: 20,
                            borderRadius: 5,
                            height: 185
                        }}>
                            <FlatList
                                style={{
                                    maxHeight: '100%'
                                }}
                                data={mesesParaSelecionar} 
                                renderItem={(mes)=>{
                                    return (
                                        <NativeButton 
                                            feedbackColor={Colors.cinza[500]} 
                                            onPress={()=>{
                                                setMesSelecionado(mes.item)
                                                setModalIsVisible(false)
                                            }}
                                            styleView={{
                                                padding: 2, 
                                                paddingTop: 12,
                                                marginBottom: 5,
                                                borderBottomColor: Colors.cinza[800],
                                                borderBottomWidth: 1,
                                                alignItems: "center"
                                            }}>
                                            <NunitoText style={{color: Colors.cinza[800], fontSize: 17}}>{nomeMesArray[mes.item.mes-1]} de {mes.item.ano}</NunitoText>
                                        </NativeButton>
                                    )
                                }}/>
                        </View>
                    </View>
                </View>
            </Modal>
        </>
    )

}

function GraficoGastoMes(
    { dadosMeses, valoresVisiveis }:
        { dadosMeses: dadoMes[], valoresVisiveis: boolean },

) {

    let graficoHeight = 150

    return (
        <View style={{ marginTop: 30, width: 255 }}>
            <View style={{
                height: graficoHeight,
                flexDirection: "row",
                width: "100%"
            }}>
                <View style={{
                    borderRightWidth: 2,
                    borderRightColor: Colors.cinza[950],
                    width: 25,
                    height: '100%',
                    position: "relative"
                }}>
                    <NunitoText style={{
                        color: Colors.cinza[950],
                        fontSize: 10,
                        position: "absolute",
                        right: -10,
                        top: -15
                    }} >(R$)</NunitoText>
                </View>
                <View style={{
                    height: '100%',
                    width: "100%",
                    flexDirection: "row",
                    justifyContent: "space-evenly",
                    alignItems: "flex-end"
                }}>
                    {processarDadosParaView(dadosMeses, graficoHeight, valoresVisiveis)}
                </View>
            </View>
            <View style={{
                flexDirection: "row",
                width: "100%"
            }}>
                <View style={{
                    width: 25,
                    height: 25,
                    borderRightWidth: 2,
                    borderTopWidth: 2,
                    borderColor: Colors.cinza[950],
                }}></View>
                <View style={{
                    height: 25,
                    borderTopWidth: 2,
                    borderColor: Colors.cinza[950],
                    width: "100%"
                }}></View>
            </View>
        </View>
    );

    function processarDadosParaView(
        dadosMeses: { mes: number, ano: number, gastoTotal: number }[],
        alturaGrafico: number, valoresVisiveis: boolean
    ) {
        let gastosEmOrdem = dadosMeses.map((d) => d.gastoTotal).sort((a, b) => {
            if (a < b)
                return -1
            else if (a === b)
                return 0
            else (a > b)
            return 1
        })

        let maiorGasto = gastosEmOrdem[gastosEmOrdem.length - 1]

        return (
            <>
                {
                    dadosMeses.map((dadoMes, index) => {
                        let percent = ((dadoMes.gastoTotal * 100) / maiorGasto) / 100

                        let color: string = "cinza";
                        if (percent < (50 / 100))
                            color = "verde"
                        else if ((50 / 100) < percent && percent < (75 / 100))
                            color = "amarelo"
                        else if (percent > (75 / 100))
                            color = "vermelho"

                        return (
                            <View key={index} style={{
                                width: 25,
                                height: percent * alturaGrafico,
                                backgroundColor: oColors[color][600],
                                justifyContent: "space-between",
                                position: "relative",
                                alignItems: "center"
                            }}>
                                <DadosSensiveisText isVisible={valoresVisiveis} style={{
                                    fontSize: 8,
                                    color: oColors[color][950],
                                    transform: [{ translateY: -10 }],
                                    width: "180%",
                                    textAlign: "center"
                                }} styleLine={{ opacity: 0 }}>{maskBRL(dadoMes.gastoTotal, false)}</DadosSensiveisText>
                                <NunitoText style={{
                                    fontSize: 10,
                                    color: oColors[color][950],
                                    transform: [{ translateY: 15 }],
                                    textAlign: "center",
                                    width: "140%"
                                }}>{maskMesAnoAbreviado(dadoMes.mes, dadoMes.ano)}</NunitoText>
                            </View>
                        )
                    })
                }
            </>
        );
    }
}

function Section({ children, backgroundColor, marginBottom }: any) {

    return (
        <View style={[styles.section, { backgroundColor: backgroundColor, marginBottom: marginBottom }]} >
            {children}
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        padding: 20,
        paddingBottom: 30
    },
    topPage: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        marginBottom: 25
    },
    buttonSelectMes: {
        backgroundColor: Colors.vermelho[50],
        paddingVertical: 8,
        paddingHorizontal: 20,
        borderRadius: 50,
        flexDirection: "row",
        alignItems: "center"
    },
    section: {
        elevation: 3,
        borderRadius: 10,
        padding: 15
    }
})
