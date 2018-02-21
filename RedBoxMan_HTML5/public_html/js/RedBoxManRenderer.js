/* 
 * This JavaScript file provides methods for clearing the
 * canvas and for rendering Red Box Man.
 */

var canvas;
var gc;
var canvasWidth;
var canvasHeight;
var imageLoaded;
var mousePositionRendering;
var redBoxManImage;
var mouseX;
var mouseY;
var imagesRedBoxManLocations;
var shapesRedBoxManLocations;

function Location(initX, initY) {
    this.x = initX;
    this.y = initY;
}

function init() {
    // GET THE CANVAS SO WE CAN USE IT WHEN WE LIKE
    canvas = document.getElementById("red_box_man_canvas");
    gc = canvas.getContext("2d");

    // MAKE SURE THE CANVAS DIMENSIONS ARE 1:1 FOR RENDERING
    canvas.width = canvas.offsetWidth;
    canvas.height = canvas.offsetHeight;
    canvasWidth = canvas.width;
    canvasHeight = canvas.height;

    // FOR RENDERING TEXT
    gc.font = "32pt Arial";

    // LOAD THE RED BOX MAN IMAGE SO WE CAN RENDER
    // IT TO THE CANVAS WHENEVER WE LIKE
    redBoxManImage = new Image();
    redBoxManImage.onload = function () {
        imageLoaded = true;
    }
    redBoxManImage.src = "./images/RedBoxMan.png";

    // BY DEFAULT WE'LL START WITH MOUSE POSITION RENDERING ON
    mousePositionRendering = true;

    // HERE'S WHERE WE'LL PUT OUR RENDERING COORDINATES
    imagesRedBoxManLocations = new Array();
    shapesRedBoxManLocations = new Array();
}

function processMouseClick(event) {
    updateMousePosition(event);
    var location = new Location(mouseX, mouseY);
    if (event.shiftKey) {
        shapesRedBoxManLocations.push(location);
        render();
    } else if (event.ctrlKey) {
        if (imageLoaded) {
            imagesRedBoxManLocations.push(location);
            render();
        }
    } else {
        clear();
    }
}

function clearCanvas() {
    gc.clearRect(0, 0, canvasWidth, canvasHeight);
}

function clear() {
    shapesRedBoxManLocations = [];
    imagesRedBoxManLocations = [];
    clearCanvas();
}

function updateMousePosition(event) {
    var rect = canvas.getBoundingClientRect();
    mouseX = event.clientX - rect.left;
    mouseY = event.clientY - rect.top;
    render();
}

function renderShapesRedBoxMan(location) {
    var headColor = "#DD0000";
    var outlineColor = "#000000";
    var headW = 115;
    var headH = 88;
    var eyeColor = "#FFFF00";
    var pupilColor = "#000000";

    // DRAW HIS RED HEAD
    gc.fillStyle = headColor;
    gc.fillRect(location.x, location.y, headW, headH);
    gc.beginPath();
    gc.strokeStyle = outlineColor;
    gc.lineWidth = 1;
    gc.rect(location.x, location.y, headW, headH);
    gc.stroke();

    // AND THEN DRAW THE REST OF HIM

    gc.fillStyle = eyeColor;
    gc.fillRect(location.x + 20, location.y + 10, headW / 4, headH / 4);
    gc.fillRect(location.x + 70, location.y + 10, headW / 4, headH / 4);

    gc.fillStyle = pupilColor;
    gc.fillRect(location.x + 30, location.y + 15, headW / 10, headH / 10);
    gc.fillRect(location.x + 80, location.y + 15, headW / 10, headH / 10);

    gc.fillStyle = outlineColor;
    gc.fillRect(location.x + 25, location.y + 60, headW - 50, headH - 70);
    gc.fillRect(location.x + 25, location.y + 88, headW - 50, headH - 60);
    gc.fillRect(location.x + 30, location.y + 116, headW - 60, headH - 75);
    gc.fillRect(location.x + 25, location.y + 129, headW - 100, headH - 80);
    gc.fillRect(location.x + 75, location.y + 129, headW - 100, headH - 80);

}

function renderImageRedBoxMan(location) {
    gc.drawImage(redBoxManImage, location.x, location.y);
}

function renderMousePositionInCanvas(event) {
    if (mousePositionRendering) {
        gc.strokeText("(" + mouseX + "," + mouseY + ")", 10, 50);
    }
}

function toggleMousePositionRendering() {
    mousePositionRendering = !mousePositionRendering;
}

function render() {
    clearCanvas();
    for (var i = 0; i < shapesRedBoxManLocations.length; i++) {
        var location = shapesRedBoxManLocations[i];
        renderShapesRedBoxMan(location);
    }
    for (var j = 0; j < imagesRedBoxManLocations.length; j++) {
        var location = imagesRedBoxManLocations[j];
        renderImageRedBoxMan(location);
    }
    renderMousePositionInCanvas();
}