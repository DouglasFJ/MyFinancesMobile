import { SvgXml } from "react-native-svg";

export default function AddIcon(
    {height, width, color} :
    {height: string, width: string, color: string}
) {
    //Não mexer no tamanho svg o react-svg já faz isso internamente
    const xml = `
    <svg width="80" height="80" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect x="35.0769" width="9.84615" height="80" rx="4.92308" fill="${color}"/>
    <rect y="44.9231" width="9.84615" height="80" rx="4.92308" transform="rotate(-90 0 44.9231)" fill="${color}"/>
    </svg>
    `
    return(<SvgXml xml={xml} width={width} height={height}/>)
    
}
