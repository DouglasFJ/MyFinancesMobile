import { StyleProp, ViewStyle } from "react-native";
import { SvgXml } from "react-native-svg";

export default function PiramideIcon(
    {height, width, firstColor, secondColor, style} :
    {height: string, width: string, firstColor: string, secondColor:string, style?: StyleProp<ViewStyle>}
) {
    const xml = `
    <svg width="80" height="80" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M9.47715 49.2025C9.79464 48.4723 10.515 48 11.3113 48H68.6887C69.485 48 70.2054 48.4723 70.5229 49.2025L78.7837 68.2026C79.358 69.5234 78.3899 71 76.9496 71H3.05042C1.61009 71 0.641984 69.5234 1.21628 68.2025L9.47715 49.2025Z" fill="${firstColor}"/>
    <path d="M12.2369 51.5964C12.3968 51.2339 12.7556 51 13.1518 51H66.8482C67.2444 51 67.6032 51.2339 67.7631 51.5964L74.3807 66.5964C74.6724 67.2574 74.1883 68 73.4658 68H6.53417C5.81169 68 5.32763 67.2574 5.61925 66.5964L12.2369 51.5964Z" fill="${secondColor}"/>
    <path d="M20.2084 29.4464C20.4551 28.5899 21.2389 28 22.1302 28H58.8698C59.7611 28 60.5449 28.5899 60.7916 29.4464L66.2645 48.4464C66.6327 49.725 65.6731 51 64.3426 51H16.6574C15.3269 51 14.3673 49.725 14.7355 48.4464L20.2084 29.4464Z" fill="${firstColor}"/>
    <path d="M22.6152 31.7271C22.7372 31.2969 23.1301 31 23.5773 31H57.4227C57.8699 31 58.2628 31.2969 58.3848 31.7271L62.639 46.7271C62.8201 47.3655 62.3405 48 61.6769 48H19.3231C18.6595 48 18.1799 47.3655 18.361 46.7271L22.6152 31.7271Z" fill="${secondColor}"/>
    <path d="M29.6576 9.57514C29.8574 8.65576 30.6711 8 31.6119 8H48.3881C49.3289 8 50.1426 8.65576 50.3424 9.57514L54.4729 28.5751C54.7439 29.8217 53.7942 31 52.5185 31H27.4815C26.2058 31 25.2561 29.8217 25.5271 28.5751L29.6576 9.57514Z" fill="${firstColor}"/>
    <path d="M31.3356 11.7983C31.4313 11.3335 31.8405 11 32.3151 11H47.208C47.672 11 48.0751 11.3192 48.1814 11.771L51.7108 26.771C51.8585 27.3987 51.3822 28 50.7374 28H29.2269C28.5935 28 28.1197 27.4187 28.2474 26.7983L31.3356 11.7983Z" fill="${secondColor}"/>
    </svg>
    `

    return(<SvgXml style={style} xml={xml} width={width} height={height}/>)
    
}