const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = parseInt(process.env.APP_PORT || '3000', 10);
const HOST = '0.0.0.0';

const indexPath = path.join(__dirname, 'index.html');
const indexHtml = fs.readFileSync(indexPath, 'utf8');

const server = http.createServer((req, res) => {
  const url = new URL(req.url, `http://${req.headers.host || 'localhost'}`);

  // Handle direct download of Android APK
  if (url.pathname === '/download/app-debug.apk' || url.pathname === '/app-debug.apk') {
    const apkCandidates = [
      path.join(__dirname, '.build-outputs', 'app-debug.apk'),
      path.join(__dirname, 'app', 'build', 'outputs', 'apk', 'debug', 'app-debug.apk')
    ];
    let foundPath = apkCandidates.find(p => fs.existsSync(p));

    if (foundPath) {
      const stat = fs.statSync(foundPath);
      res.writeHead(200, {
        'Content-Type': 'application/vnd.android.package-archive',
        'Content-Length': stat.size,
        'Content-Disposition': 'attachment; filename="TileForge.apk"'
      });
      return fs.createReadStream(foundPath).pipe(res);
    }
  }

  // Handle Health Check
  if (url.pathname === '/health' || url.pathname === '/_health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    return res.end(JSON.stringify({ status: 'ok', app: 'Tile Forge' }));
  }

  // Serve Main Web Game
  res.writeHead(200, {
    'Content-Type': 'text/html; charset=UTF-8',
    'Cache-Control': 'no-cache'
  });
  res.end(indexHtml);
});

server.listen(PORT, HOST, () => {
  console.log(`[Tile Forge] Web server is live at http://${HOST}:${PORT}`);
});
