export const medals = [
  {
    id: 1,
    name: '萌芽勋章',
    description: '累计打卡7天获得',
    requiredDays: 7,
    svgContent: `
      <defs>
        <linearGradient id="leafGradient" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style="stop-color:#AED581;stop-opacity:1" />
          <stop offset="100%" style="stop-color:#7CB342;stop-opacity:1" />
        </linearGradient>
      </defs>
      <path d="M32 58 Q32 40 32 35" stroke="#795548" stroke-width="3" stroke-linecap="round" fill="none"/>
      <path d="M32 35 Q12 35 12 15 Q32 20 32 35 Z" fill="url(#leafGradient)" />
      <path d="M32 35 Q52 35 52 15 Q32 20 32 35 Z" fill="url(#leafGradient)" />
    `,
    bgColor: '#FFF9C4'
  },
  {
    id: 2,
    name: '成长豆勋章',
    description: '累计打卡15天获得',
    requiredDays: 15,
    svgContent: `
      <defs>
        <linearGradient id="beanGradient" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style="stop-color:#C5E1A5;stop-opacity:1" />
          <stop offset="100%" style="stop-color:#8BC34A;stop-opacity:1" />
        </linearGradient>
      </defs>
      <path d="M32 30 Q40 15 50 20" stroke="#7CB342" stroke-width="4" stroke-linecap="round" fill="none"/>
      <path d="M50 20 Q45 10 38 18 Z" fill="#7CB342" />
      <ellipse cx="32" cy="40" rx="16" ry="12" fill="url(#beanGradient)" />
      <path d="M25 38 Q32 45 38 38" stroke="#558B2F" stroke-width="2" stroke-linecap="round" fill="none" opacity="0.5"/>
    `,
    bgColor: '#DCEDC8'
  },
  {
    id: 3,
    name: '小树勋章',
    description: '累计打卡30天获得',
    requiredDays: 30,
    svgContent: `
      <defs>
        <radialGradient id="treeGradient" cx="50%" cy="50%" r="50%">
          <stop offset="0%" style="stop-color:#81C784;stop-opacity:1" />
          <stop offset="100%" style="stop-color:#388E3C;stop-opacity:1" />
        </radialGradient>
      </defs>
      <path d="M30 60 L34 60 L34 35 L30 35 Z" fill="#795548" />
      <circle cx="32" cy="25" r="18" fill="url(#treeGradient)" />
      <circle cx="20" cy="35" r="12" fill="url(#treeGradient)" />
      <circle cx="44" cy="35" r="12" fill="url(#treeGradient)" />
    `,
    bgColor: '#B3E5FC'
  },
  {
    id: 4,
    name: '繁花勋章',
    description: '累计打卡90天获得',
    requiredDays: 90,
    svgContent: `
      <defs>
        <radialGradient id="flowerGradient" cx="50%" cy="50%" r="50%">
          <stop offset="0%" style="stop-color:#F8BBD0;stop-opacity:1" />
          <stop offset="100%" style="stop-color:#E91E63;stop-opacity:1" />
        </radialGradient>
      </defs>
      <path d="M32 60 Q32 45 32 32" stroke="#4CAF50" stroke-width="3" fill="none" />
      <circle cx="32" cy="16" r="10" fill="url(#flowerGradient)" />
      <circle cx="47" cy="27" r="10" fill="url(#flowerGradient)" />
      <circle cx="41" cy="43" r="10" fill="url(#flowerGradient)" />
      <circle cx="23" cy="43" r="10" fill="url(#flowerGradient)" />
      <circle cx="17" cy="27" r="10" fill="url(#flowerGradient)" />
      <circle cx="32" cy="31" r="8" fill="#FFEB3B" />
    `,
    bgColor: '#E1BEE7'
  },
  {
    id: 5,
    name: '森林勋章',
    description: '累计打卡180天获得',
    requiredDays: 180,
    svgContent: `
      <defs>
        <linearGradient id="pineGradient" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" style="stop-color:#4DB6AC;stop-opacity:1" />
          <stop offset="100%" style="stop-color:#00796B;stop-opacity:1" />
        </linearGradient>
      </defs>
      <path d="M16 55 L18 55 L18 40 L16 40 Z" fill="#5D4037" />
      <path d="M17 25 L25 45 L9 45 Z" fill="url(#pineGradient)" />
      <path d="M48 55 L50 55 L50 35 L48 35 Z" fill="#5D4037" />
      <path d="M49 18 L59 40 L39 40 Z" fill="url(#pineGradient)" />
      <path d="M31 60 L35 60 L35 30 L31 30 Z" fill="#5D4037" />
      <path d="M33 10 L47 38 L19 38 Z" fill="url(#pineGradient)" />
    `,
    bgColor: '#B2DFDB'
  },
  {
    id: 6,
    name: '皇冠勋章',
    description: '累计打卡365天获得',
    requiredDays: 365,
    svgContent: `
      <defs>
        <linearGradient id="goldGradient" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style="stop-color:#FFF59D;stop-opacity:1" />
          <stop offset="50%" style="stop-color:#FFC107;stop-opacity:1" />
          <stop offset="100%" style="stop-color:#FF9800;stop-opacity:1" />
        </linearGradient>
      </defs>
      <path d="M10 20 L20 35 L32 10 L44 35 L54 20 L50 50 L14 50 Z" fill="url(#goldGradient)" stroke="#F57C00" stroke-width="2" stroke-linejoin="round"/>
      <circle cx="10" cy="20" r="4" fill="#F44336" />
      <circle cx="32" cy="10" r="5" fill="#2196F3" />
      <circle cx="54" cy="20" r="4" fill="#F44336" />
      <circle cx="32" cy="40" r="3" fill="#4CAF50" />
    `,
    bgColor: '#FFE082'
  }
];
