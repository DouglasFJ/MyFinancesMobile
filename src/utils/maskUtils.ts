import { mask } from "react-native-mask-text";
import { nomeMesArray } from "../constantes/MesesConstante";

export function maskBRL(number: number, prefix?: boolean) {
    return mask(number*100,undefined,"currency",{
        prefix: prefix || prefix === undefined?"R$ ":"", 
        decimalSeparator: ",", 
        precision: 2,
    })
}

export function maskMesAnoAbreviado(mes: number, ano: number) {
    let nomeMes = nomeMesArray[mes-1]
    let sAno: string = ano.toString()

    return `${nomeMes.substring(0, 3)}/${sAno.substring(2, 4)}`
}