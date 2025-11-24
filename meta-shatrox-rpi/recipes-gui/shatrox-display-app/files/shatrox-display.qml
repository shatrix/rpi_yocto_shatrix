import QtQuick 2.15
import QtQuick.Controls 2.15

ApplicationWindow {
    id: root
    visible: true
    width: 480
    height: 320
    title: "SHATROX AI Robot"
    color: "#0a0a0a"
    
    // Robot Face Layout
    Column {
        anchors.fill: parent
        anchors.margins: 20
        spacing: 15
        
        // Eyes
        Row {
            anchors.horizontalCenter: parent.horizontalCenter
            spacing: 80
            
            // Left Eye
            Rectangle {
                id: leftEye
                width: 60
                height: 60
                radius: 30
                color: "#1a1a1a"
                border.color: "#00ff00"
                border.width: 3
                
                Rectangle {
                    id: leftPupil
                    width: 20
                    height: 20
                    radius: 10
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
            }
            
            // Right Eye
            Rectangle {
                id: rightEye
                width: 60
                height: 60
                radius: 30
                color: "#1a1a1a"
                border.color: "#00ff00"
                border.width: 3
                
                Rectangle {
                    id: rightPupil
                    width: 20
                    height: 20
                    radius: 10
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
            }
        }
        
        // Nose
        Rectangle {
            anchors.horizontalCenter: parent.horizontalCenter
            width: 15
            height: 25
            radius: 8
            color: "#1a1a1a"
            border.color: "#00ff00"
            border.width: 2
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
    
    // Log file monitor
    Timer {
        id: logMonitor
        interval: 500
        running: true
        repeat: true
        
        property string logContent: "╔═══════════════════════════════════════╗\n║  SHATROX AI Robot Ready              ║\n╚═══════════════════════════════════════╝\n\nWaiting for button press..."
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
                var lines = content.split('\n')
                if (lines.length > maxLines) {
                    lines = lines.slice(lines.length - maxLines)
                    content = lines.join('\n')
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
    }
}
