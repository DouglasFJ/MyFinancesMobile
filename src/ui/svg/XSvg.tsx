import { SvgXml } from "react-native-svg";

export default function XSvg(
    {height, width, color} :
    {height: string, width: string, color: string}
) {

    const xml = `
    <svg width="37" height="38" viewBox="0 0 37 38" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect y="4.65686" width="6" height="46" rx="3" transform="rotate(-45 0 4.65686)" fill="${color}"/>
    <rect x="32.5269" width="6" height="46" rx="3" transform="rotate(45 32.5269 0)" fill="${color}"/>
    </svg>    
    `
    return(<SvgXml xml={xml} width={width} height={height}/>)
    
}
