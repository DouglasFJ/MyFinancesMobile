import { SvgXml } from "react-native-svg";

export default function OlhoFechadoSvg(
    {height, width, color} :
    {height: string, width: string, color: string}
) {

    const xml = `
    <svg width="90" height="60" viewBox="0 0 90 60" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M18 33.5C29.8426 32.2063 56.4567 32.1276 72 33.5" stroke="${color}" stroke-width="4" stroke-linecap="round"/>
    <path d="M72 34.5C60.1574 35.7937 33.5433 35.8724 18 34.5" stroke="${color}" stroke-width="4" stroke-linecap="round"/>
    <path d="M64 33C66.4842 29.8098 66.2338 30.3834 68 26.5" stroke="${color}" stroke-width="4" stroke-linecap="round"/>
    <path d="M27.5 34C25.2558 30.5548 23.8498 30.7881 23 26" stroke="${color}" stroke-width="4" stroke-linecap="round"/>
    <path d="M46 32.5V24" stroke="${color}" stroke-width="4" stroke-linecap="round"/>
    </svg>          
    `
    return(<SvgXml xml={xml} width={width} height={height}/>)
    
}
