import { SvgXml } from "react-native-svg";

export default function CartaoIcon(
    {height, width, colorPrimary, colorSecondary} :
    {height: string, width: string, colorPrimary: string, colorSecondary:string}
) {
    //Não mexer no tamanho svg o react-svg já faz isso internamente
    const xml = `
    <svg width="80" height="80" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <g clip-path="url(#clip0_31_62)">
    <rect width="80" height="80" rx="8" fill="${colorPrimary}"/>
    <rect y="23" width="80" height="8" fill="${colorSecondary}"/>
    <rect x="9" y="62" width="16" height="8" rx="4" fill="${colorSecondary}"/>
    </g>
    <defs>
    <clipPath id="clip0_31_62">
    <rect width="80" height="80" fill="white"/>
    </clipPath>
    </defs>
    </svg>
    `
    return(<SvgXml xml={xml} width={width} height={height}/>)
    
}
