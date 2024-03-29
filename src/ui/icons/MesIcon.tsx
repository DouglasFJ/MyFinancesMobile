import { SvgXml } from "react-native-svg";

export default function MesIcon(
    {height, width, colorPrimary, colorSecondary} :
    {height: string, width: string, colorPrimary: string, colorSecondary:string}
) {
    //FIXME: arrumar
    const xml = `
    <svg width="80" height="80" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <g clip-path="url(#clip0_31_57)">
    <rect y="11" width="80" height="68.545" rx="8" fill="${colorPrimary}"/>
    <rect x="8" width="14" height="22.9091" rx="7" fill="${colorSecondary}"/>
    <rect x="10.5455" y="2.54547" width="8.90909" height="17.8182" rx="4.45455" fill="${colorPrimary}"/>
    <rect x="42" width="14" height="22.9091" rx="7" fill="${colorSecondary}"/>
    <rect x="44.5455" y="2.54547" width="8.90909" height="17.8182" rx="4.45455" fill="${colorPrimary}"/>
    <rect x="25" width="14" height="22.9091" rx="7" fill="${colorSecondary}"/>
    <rect x="27.5455" y="2.54547" width="8.90909" height="17.8182" rx="4.45455" fill="${colorPrimary}"/>
    <rect x="59" width="14" height="22.9091" rx="7" fill="${colorSecondary}"/>
    <rect x="61.5455" y="2.54547" width="8.90909" height="17.8182" rx="4.45455" fill="${colorPrimary}"/>
    <path d="M10 28H71V65C71 67.2091 69.2091 69 67 69H14C11.7909 69 10 67.2091 10 65V28Z" fill="${colorSecondary}"/>
    </g>
    <defs>
    <clipPath id="clip0_31_57">
    <rect width="80" height="80" fill="white"/>
    </clipPath>
    </defs>
    </svg>
    `
    return(<SvgXml xml={xml} width={width} height={height}/>)
    
}
