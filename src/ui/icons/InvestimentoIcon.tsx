import { StyleProp, ViewStyle } from "react-native";
import { SvgXml } from "react-native-svg";

export default function InvestimentoIcon(
    {height, width, firstColor, secondColor, thirdColor, style} :
    {height: string, width: string, firstColor: string, secondColor:string, thirdColor:string, style?: StyleProp<ViewStyle>}
) {
    const xml = `
    <svg width="80" height="80" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
    <ellipse cx="40.2507" cy="52.9146" rx="32.1951" ry="14.0854" fill="${firstColor}"/>
    <ellipse cx="40.2507" cy="52.9146" rx="29.2233" ry="10.061" fill="${secondColor}"/>
    <ellipse cx="40.2507" cy="43.5244" rx="32.1951" ry="14.0854" fill="${firstColor}"/>
    <ellipse cx="40.2507" cy="43.5244" rx="29.2233" ry="10.061" fill="${secondColor}"/>
    <ellipse cx="40.2507" cy="35.4756" rx="32.1951" ry="14.0854" fill="${firstColor}"/>
    <ellipse cx="40.2507" cy="35.4756" rx="29.2233" ry="10.061" fill="${secondColor}"/>
    <ellipse cx="40.2507" cy="26.0854" rx="32.1951" ry="14.0854" fill="${firstColor}"/>
    <ellipse cx="40.2507" cy="26.0854" rx="29.2233" ry="10.061" fill="${secondColor}"/>
    <path d="M2.22346 62.0836C0.633222 63.617 0.587181 66.1493 2.12062 67.7395C3.65406 69.3298 6.18631 69.3758 7.77654 67.8424L2.22346 62.0836ZM34.4005 36.6125L37.3815 33.9453C36.6569 33.1354 35.6339 32.6552 34.5479 32.6152C33.4618 32.5751 32.4063 32.9787 31.624 33.7331L34.4005 36.6125ZM43.3257 46.5876L40.3447 49.2548C41.0816 50.0783 42.1262 50.5604 43.231 50.5865C44.3357 50.6127 45.402 50.1807 46.177 49.393L43.3257 46.5876ZM75.3513 14.037H79.3513V10.037H75.3513V14.037ZM71.3513 25.5873C71.3513 27.7964 73.1421 29.5873 75.3513 29.5873C77.5604 29.5873 79.3513 27.7964 79.3513 25.5873H71.3513ZM63.8011 10.037C61.5919 10.037 59.8011 11.8279 59.8011 14.037C59.8011 16.2462 61.5919 18.037 63.8011 18.037V10.037ZM7.77654 67.8424L37.1771 39.4918L31.624 33.7331L2.22346 62.0836L7.77654 67.8424ZM31.4196 39.2796L40.3447 49.2548L46.3067 43.9205L37.3815 33.9453L31.4196 39.2796ZM46.177 49.393L78.2026 16.8424L72.5 11.2317L40.4744 43.7823L46.177 49.393ZM71.3513 14.037V25.5873H79.3513V14.037H71.3513ZM75.3513 10.037H63.8011V18.037H75.3513V10.037Z" fill="${firstColor}"/>
    <path d="M3.61173 63.5233C2.81661 64.29 2.79359 65.5561 3.56031 66.3512C4.32703 67.1464 5.59315 67.1694 6.38827 66.4027L3.61173 63.5233ZM34.4005 36.6125L35.891 35.2789C35.5287 34.8739 35.0172 34.6338 34.4742 34.6138C33.9312 34.5938 33.4034 34.7956 33.0123 35.1728L34.4005 36.6125ZM43.3257 46.5876L41.8352 47.9212C42.2036 48.333 42.726 48.574 43.2783 48.5871C43.8307 48.6002 44.3639 48.3842 44.7514 47.9903L43.3257 46.5876ZM75.3513 14.037H77.3513V12.037H75.3513V14.037ZM73.3513 25.5873C73.3513 26.6918 74.2467 27.5873 75.3513 27.5873C76.4558 27.5873 77.3513 26.6918 77.3513 25.5873H73.3513ZM63.8011 12.037C62.6965 12.037 61.8011 12.9325 61.8011 14.037C61.8011 15.1416 62.6965 16.037 63.8011 16.037V12.037ZM6.38827 66.4027L35.7888 38.0521L33.0123 35.1728L3.61173 63.5233L6.38827 66.4027ZM32.9101 37.946L41.8352 47.9212L44.8162 45.2541L35.891 35.2789L32.9101 37.946ZM44.7514 47.9903L76.7769 15.4397L73.9256 12.6344L41.9 45.185L44.7514 47.9903ZM73.3513 14.037V25.5873H77.3513V14.037H73.3513ZM75.3513 12.037H63.8011V16.037H75.3513V12.037Z" fill="${thirdColor}"/>
    </svg>
    `

    return(<SvgXml style={style} xml={xml} width={width} height={height}/>)
    
}