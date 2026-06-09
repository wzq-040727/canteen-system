const sharp = require('sharp');
const path = require('path');

async function gen(name, w, h, c1, c2, dir = 'diagonal') {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}">
    <defs>
      <linearGradient id="g" x1="0%" y1="0%" x2="${dir === 'diagonal' ? '100%' : '100%'}" y2="${dir === 'diagonal' ? '100%' : '0%'}">
        <stop offset="0%" style="stop-color:${c1}"/>
        <stop offset="100%" style="stop-color:${c2}"/>
      </linearGradient>
    </defs>
    <rect width="100%" height="100%" fill="url(#g)"/>
  </svg>`;
  await sharp(Buffer.from(svg)).png().toFile(path.join('E:/毕设/毕设文档', name));
  console.log('Generated:', name);
}

(async () => {
  // Title/divider bg: dark navy gradient
  await gen('bg_dark.png', 1920, 1080, '#0d1b2a', '#1b3a5c', 'diagonal');
  // Content bg: very subtle light blue-gray gradient
  await gen('bg_light.png', 1920, 1080, '#f8fafc', '#eef2f7', 'vertical');
  // Section divider: medium navy
  await gen('bg_section.png', 1920, 1080, '#132c4a', '#1e4d7b', 'diagonal');
  // Conclusions bg: dark with blue tint
  await gen('bg_conclusion.png', 1920, 1080, '#0a1929', '#153050', 'diagonal');
  // Accent bar (vertical)
  await gen('accent_bar.png', 30, 1080, '#2563eb', '#1d4ed8', 'vertical');
  // Top header strip
  await gen('header_strip.png', 1920, 60, '#1e3a5f', '#2563eb', 'horizontal');
})();
