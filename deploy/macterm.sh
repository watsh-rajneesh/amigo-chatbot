#!/usr/bin/env bash
#!/bin/sh
osascript <<END
tell application "Terminal"
    do script "cd \"`pwd`\";$1"
end tell
END