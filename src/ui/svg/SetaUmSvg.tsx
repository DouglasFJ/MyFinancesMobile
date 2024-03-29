import { SvgXml } from "react-native-svg";

export default function SetaUmSvg(
    {height, width, color} :
    {height: string, width: string, color: string}
) {
    //Não mexer no tamanho svg o react-svg já faz isso internamente
    const xml = `
    <svg width="44" height="27" viewBox="0 0 44 27" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M2.45994 7.63828C1.10135 6.2797 1.10135 4.07699 2.45994 2.71841C3.81852 1.35982 6.02122 1.35982 7.37981 2.71841L26.4443 21.7829L21.5244 26.7028L2.45994 7.63828Z" fill="${color}"/>
    <path d="M35.7656 2.45994C37.1242 1.10135 39.3269 1.10135 40.6855 2.45994C42.0441 3.81852 42.0441 6.02122 40.6855 7.37981L21.621 26.4443L16.7011 21.5244L35.7656 2.45994Z" fill="${color}"/>
    </svg>
    
    `
    return(<SvgXml xml={xml} width={width} height={height}/>)
    
}
