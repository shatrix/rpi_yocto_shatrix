import QtQuick 2.15
import QtQuick.Controls 2.15

ApplicationWindow {
    id: root
    visible: true
    width: 480
    height: 320
    title: "SHATROX AI Robot"
    color: "#0a0a0a"
    
    // Helper function to add laugh message to log
    function addLaughToLog() {
        var xhr = new XMLHttpRequest()
        xhr.open("GET", "file://" + logMonitor.logFile, false)
        var currentLog = ""
        try {
            xhr.send()
            if (xhr.status === 200 || xhr.status === 0) {
                currentLog = xhr.responseText
            }
        } catch(e) {}
        
        // Append laugh message  
        var laughMsg = "\n😄 HA HA HA HA HA! 😄\n"
        var newLog = currentLog + laughMsg
        
        // Write back to log file
        var writeXhr = new XMLHttpRequest()
        writeXhr.open("PUT", "file://" + logMonitor.logFile, false)
        try {
            writeXhr.send(newLog)
        } catch(e) {
            console.log("Could not write laugh to log:", e)
        }
    }
    
    // Touch monitor (ADS7846 driver workaround - monitors IRQ count)
    Timer {
        id: touchMonitor
        interval: 100
        running: true
        repeat: true
        
        property string touchFile: "/tmp/shatrox-touch-trigger"
        property string lastTouch: ""
        
        onTriggered: {
            var content = fileReader.readFile(touchFile)
            if (content !== "" && content !== lastTouch) {
                lastTouch = content
                console.log("TOUCH DETECTED via IRQ monitor!")
                addLaughToLog()
                laughAnimation.start()
            }
        }
    }
    
    
    // Robot Face Layout
    Column {
        anchors.fill: parent
        anchors.margins: 10
        spacing: 10
        
        // Eyes (at top edges)
        Row {
            anchors.horizontalCenter: parent.horizontalCenter
            spacing: 180
            
            // Left Eye
            Rectangle {
                id: leftEye
                width: 100
                height: 100
                radius: 50
                color: "#1a1a1a"
                border.color: "#00ff00"
                border.width: 3
                
                Rectangle {
                    id: leftPupil
                    width: 25
                    height: 25
                    radius: 12
                    color: "#00ff00"
                    anchors.centerIn: parent
                    
                    SequentialAnimation on y {
                        running: true
                        loops: Animation.Infinite
                        NumberAnimation { to: leftEye.height * 0.4; duration: 2000 }
                        NumberAnimation { to: leftEye.height * 0.5; duration: 2000 }
                        NumberAnimation { to: leftEye.height * 0.6; duration: 2000 }
                        NumberAnimation { to: leftEye.height * 0.5; duration: 2000 }
                    }
                }
                
                // Blink eyelid
                Rectangle {
                    id: leftEyelid
                    width: parent.width
                    height: 0
                    color: "#0a0a0a"
                    anchors.top: parent.top
                }
                
                // Touch handler
                MouseArea {
                    anchors.fill: parent
                    z: 10
                    onClicked: {
                        console.log("Eye tapped - laughing!")
                        addLaughToLog()
                        laughAnimation.start()
                    }
                }
            }
            
            // Right Eye
            Rectangle {
                id: rightEye
                width: 100
                height: 100
                radius: 50
                color: "#1a1a1a"
                border.color: "#00ff00"
                border.width: 3
                
                Rectangle {
                    id: rightPupil
                    width: 25
                    height: 25
                    radius: 12
                    color: "#00ff00"
                    anchors.centerIn: parent
                    
                    SequentialAnimation on y {
                        running: true
                        loops: Animation.Infinite
                        NumberAnimation { to: rightEye.height * 0.4; duration: 2000 }
                        NumberAnimation { to: rightEye.height * 0.5; duration: 2000 }
                        NumberAnimation { to: rightEye.height * 0.6; duration: 2000 }
                        NumberAnimation { to: rightEye.height * 0.5; duration: 2000 }
                    }
                }
                
                // Blink eyelid
                Rectangle {
                    id: rightEyelid
                    width: parent.width
                    height: 0
                    color: "#0a0a0a"
                    anchors.top: parent.top
                }
                
                // Touch handler
                MouseArea {
                    anchors.fill: parent
                    z: 10
                    onClicked: {
                        console.log("Eye tapped - laughing!")
                        addLaughToLog()
                        laughAnimation.start()
                    }
                }
            }
        }
        
        // CPU Temperature Display (Nose position)
        Rectangle {
            anchors.horizontalCenter: parent.horizontalCenter
            width: 50
            height: 35
            radius: 6
            color: "#1a1a1a"
            border.color: cpuTempMonitor.tempColor
            border.width: 2
            
            // Glow effect
            Rectangle {
                anchors.fill: parent
                radius: parent.radius
                color: "transparent"
                border.color: cpuTempMonitor.tempColor
                border.width: 1
                opacity: 0.2
                scale: 1.15
            }
            
            Row {
                anchors.centerIn: parent
                spacing: 3
                
                // Temperature value (rounded, no decimals)
                Text {
                    text: Math.round(cpuTempMonitor.temperature)
                    font.family: "Monospace"
                    font.pixelSize: 14
                    font.bold: true
                    color: cpuTempMonitor.tempColor
                    anchors.verticalCenter: parent.verticalCenter
                }
            }
        }
        
        // Mouth (Text Display Area)
        Rectangle {
            id: mouthArea
            width: parent.width - 40
            height: 150
            radius: 15
            color: "#1a1a1a"
            border.color: "#00ff00"
            border.width: 3
            anchors.horizontalCenter: parent.horizontalCenter
            
            // Pulsing border when speaking
            SequentialAnimation on border.width {
                running: logMonitor.hasNewContent
                loops: Animation.Infinite
                NumberAnimation { from: 3; to: 5; duration: 300 }
                NumberAnimation { from: 5; to: 3; duration: 300 }
            }
            
            ScrollView {
                id: scrollView
                anchors.fill: parent
                anchors.margins: 10
                clip: true
                
                ScrollBar.vertical.policy: ScrollBar.AsNeeded
                ScrollBar.horizontal.policy: ScrollBar.AlwaysOff
                
                TextArea {
                    id: textDisplay
                    width: scrollView.width - 20
                    readOnly: true
                    wrapMode: TextArea.Wrap
                    selectByMouse: false
                    selectByKeyboard: false
                    
                    font.family: "Monospace"
                    font.pixelSize: 11
                    color: "#00ff00"
                    
                    background: Rectangle {
                        color: "transparent"
                    }
                    
                    text: logMonitor.logContent
                    
                    // Auto-scroll to bottom when text changes
                    onTextChanged: {
                        // Force scroll to bottom
                        Qt.callLater(function() {
                            scrollView.contentItem.contentY = scrollView.contentItem.contentHeight - scrollView.height
                        })
                    }
                }
            }
        }
    }
    
    // Random blink timer
    Timer {
        id: blinkTimer
        interval: 3000 + Math.random() * 2000
        running: true
        repeat: true
        
        onTriggered: {
            blinkAnimation.start()
            interval = 3000 + Math.random() * 2000  // Randomize next blink
        }
    }
    
    // Blink animation
    SequentialAnimation {
        id: blinkAnimation
        
        ParallelAnimation {
            NumberAnimation {
                target: leftEyelid
                property: "height"
                to: leftEye.height
                duration: 100
            }
            NumberAnimation {
                target: rightEyelid
                property: "height"
                to: rightEye.height
                duration: 100
            }
        }
        
        PauseAnimation { duration: 80 }
        
        ParallelAnimation {
            NumberAnimation {
                target: leftEyelid
                property: "height"
                to: 0
                duration: 100
            }
            NumberAnimation {
                target: rightEyelid
                property: "height"
                to: 0
                duration: 100
            }
        }
    }
    
    // Laugh animation (quick repeated blinks)
    SequentialAnimation {
        id: laughAnimation
        
        // Play laugh sound at start
        ScriptAction {
            script: {
                // Trigger laugh sound using speak command
                var component = Qt.createQmlObject('import QtQuick 2.15; import Qt.labs.platform 1.1; Process { onFinished: destroy() }',
                                                   root, "laughProcess")
                // Use Piper TTS to say "ha ha ha ha" with expression
                laughSound.playLaugh()
            }
        }
        
        // Three quick blinks
        ParallelAnimation {
            NumberAnimation { target: leftEyelid; property: "height"; to: leftEye.height; duration: 80 }
            NumberAnimation { target: rightEyelid; property: "height"; to: rightEye.height; duration: 80 }
        }
        PauseAnimation { duration: 60 }
        ParallelAnimation {
            NumberAnimation { target: leftEyelid; property: "height"; to: 0; duration: 80 }
            NumberAnimation { target: rightEyelid; property: "height"; to: 0; duration: 80 }
        }
        PauseAnimation { duration: 100 }
        
        ParallelAnimation {
            NumberAnimation { target: leftEyelid; property: "height"; to: leftEye.height; duration: 80 }
            NumberAnimation { target: rightEyelid; property: "height"; to: rightEye.height; duration: 80 }
        }
        PauseAnimation { duration: 60 }
        ParallelAnimation {
            NumberAnimation { target: leftEyelid; property: "height"; to: 0; duration: 80 }
            NumberAnimation { target: rightEyelid; property: "height"; to: 0; duration: 80 }
        }
        PauseAnimation { duration: 100 }
        
        ParallelAnimation {
            NumberAnimation { target: leftEyelid; property: "height"; to: leftEye.height; duration: 80 }
            NumberAnimation { target: rightEyelid; property: "height"; to: rightEye.height; duration: 80 }
        }
        PauseAnimation { duration: 60 }
        ParallelAnimation {
            NumberAnimation { target: leftEyelid; property: "height"; to: 0; duration: 80 }
            NumberAnimation { target: rightEyelid; property: "height"; to: 0; duration: 80 }
        }
    }
    
    // Laugh sound handler
    QtObject {
        id: laughSound
        
        function playLaugh() {
            console.log("Triggering laugh sound...")
            // Write trigger file - a monitor script can watch for this
            var xhr = new XMLHttpRequest()
            xhr.open("PUT", "file:///tmp/shatrox-laugh-trigger", false)
            try {
                xhr.send(new Date().toString() + "\\n")
            } catch(e) {
                console.log("Could not write laugh trigger:", e)
            }
        }
    }
    
    
    // Log file monitor
    Timer {
        id: logMonitor
        interval: 500
        running: true
        repeat: true
        
        property string logContent: "╔═══════════════════════════════════════╗\\n║  SHATROX AI Robot Ready              ║\\n╚═══════════════════════════════════════╝\\n\\nWaiting for button press..."
        property string previousContent: ""
        property string logFile: "/tmp/shatrox-display.log"
        property int maxLines: 50
        property bool hasNewContent: false
        
        onTriggered: {
            var content = fileReader.readFile(logFile)
            if (content !== "" && content !== logContent) {
                // Detect new content
                hasNewContent = (content.length > previousContent.length)
                previousContent = logContent
                
                // Keep only last maxLines
                var lines = content.split('\\n')
                if (lines.length > maxLines) {
                    lines = lines.slice(lines.length - maxLines)
                    content = lines.join('\\n')
                }
                logContent = content
                
                // Reset new content flag after animation
                if (hasNewContent) {
                    newContentTimer.restart()
                }
            }
        }
    }
    
    // Timer to reset new content flag
    Timer {
        id: newContentTimer
        interval: 2000
        onTriggered: logMonitor.hasNewContent = false
    }
    

    
    // Temperature monitoring timer
    Timer {
        id: cpuTempMonitor
        interval: 500
        running: true
        repeat: true
        
        property real temperature: 0.0
        property string tempColor: "#00ff00"
        property bool fanActive: false
        property string thermalFile: "/sys/class/thermal/thermal_zone0/temp"
        
        function updateColor() {
            if (temperature < 50) {
                tempColor = "#00ff00"
            } else if (temperature < 60) {
                tempColor = "#ffff00"
            } else if (temperature < 70) {
                tempColor = "#ff8800"
            } else {
                tempColor = "#ff0000"
            }
            fanActive = (temperature >= 40)
        }
        
        onTriggered: {
            var content = fileReader.readFile(thermalFile)
            if (content !== "") {
                var tempMilliC = parseInt(content.trim())
                if (!isNaN(tempMilliC)) {
                    temperature = tempMilliC / 1000.0
                    updateColor()
                }
            }
        }
    }
    
    // File reader helper
    QtObject {
        id: fileReader
        
        function readFile(filePath) {
            var xhr = new XMLHttpRequest()
            xhr.open("GET", "file://" + filePath, false)
            try {
                xhr.send()
                if (xhr.status === 200 || xhr.status === 0) {
                    return xhr.responseText
                }
            } catch (e) {
                console.log("Error reading file:", e)
            }
            return ""
        }
    }
    
    Component.onCompleted: {
        console.log("SHATROX Robot Face Display Started")
        console.log("Monitoring:", logMonitor.logFile)
        console.log("Temperature sensor:", cpuTempMonitor.thermalFile)
    }
}
