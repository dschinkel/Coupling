const childProcess = require('child_process');

const fork = childProcess.fork(__dirname + '/test-run', process.argv.slice(2), {
  // silent: true,
  stdio: 'pipe',
  env: process.env
});

fork.on('exit', function (exit) {
  process.exit(exit);
});

const readline = require('readline');
const rl = readline.createInterface({
  input: fork.stdout,
  output: process.stdout,
  terminal: false
});

rl.on('line', function (line) {
  try {
    const json = JSON.parse(line);

    if (json.properties.type === 'TestEnd') {
      process.stdout.write('.');

      if (json.properties.status === 'failed') {
        process.stdout.write('F\n');
        console.log(json.properties.failures)
      }
    }
  } catch (e) {
  }
});

const rl2 = readline.createInterface({
  input: fork.stderr,
  output: process.stdout,
  terminal: false
});

rl2.on('line', function (line) {
  console.log('pipe', line);
});