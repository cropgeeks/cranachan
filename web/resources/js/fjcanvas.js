/*jslint browser:true */
/*jslint devel:true */
"use strict";

// Variables for referring to the genotype canvas
var canvas;
var ctx;

// Mouse related variables
var dragStartX = null;
var dragStartY = null;
var dragging = false;
var translatedX = 0;
var translatedY = 0;

// Variables to keep track of where we are in the data
var lineStart = 0;
var lineEnd = 0;
var alleleStart = 0;
var alleleEnd = 0;

var boxSize = 16;

var font = "16px monospaced";
var fontSize = 100;

var stateTable = new Map();
var lineNames = [];
var lineData = new Map();
var colorStamps = [];

var colors = {
    greenLight: 'rgb(171,255,171)',
    greenDark: 'rgb(86,179,86)',
    redLight: 'rgb(255,171,171)',
    redDark: 'rgb(179,86,86)',
    blueLight: 'rgb(171,171,255)',
    blueDark: 'rgb(86,86,179)',
    orangeLight: 'rgb(255,228,171)',
    orangeDark: 'rgb(179,114,86)',
    white: 'rgb(255,255,255)'
};

var nucleotides = new Map();
nucleotides.set('A', new Nucleotide('A', colors.greenLight, colors.greenDark));
nucleotides.set('G', new Nucleotide('G', colors.redLight, colors.redDark));
nucleotides.set('T', new Nucleotide('T', colors.blueLight, colors.blueDark));
nucleotides.set('C', new Nucleotide('C', colors.orangeLight, colors.orangeDark));
nucleotides.set('', new Nucleotide('', colors.white, colors.white));

function Nucleotide(allele, colorLight, colorDark)
{
    this.allele = allele || '';
    this.colorLight = colorLight || 'rgb(255,255,255)';
    this.colorDark = colorDark || 'rgb(255,255,255)';
}

function HomozygousColorState(nucleotide)
{
    this.genotype = nucleotide.allele;
    this.colorLight = nucleotide.colorLight;
    this.colorDark = nucleotide.colorDark;
    this.buffer = document.createElement('canvas');
    this.buffer.width = boxSize;
    this.buffer.height = boxSize;
    drawGradientSquare(this.buffer, this.colorLight, this.colorDark, this.genotype);
}

function HeterozygousColorState(nucleotide, nucleotide2)
{
    this.genotype = nucleotide.allele + "/" + nucleotide2.allele;
    this.colorLight = nucleotide.colorLight;
    this.colorDark = nucleotide.colorDark;
    this.colorLight2 = nucleotide2.colorLight;
    this.colorDark2 = nucleotide2.colorDark;
    this.buffer = document.createElement('canvas');
    this.buffer.width = boxSize;
    this.buffer.height = boxSize;
    drawHetSquare(this.buffer, this.colorLight, this.colorDark, this.colorLight2, this.colorDark2, this.genotype);
}

function loadGenotypeData(filepath) {
    console.log("load genotype data");
    console.log(filepath);

    fetch(filepath)
        .then(function (response) {
            if (response.status !== 200) {
                console.log("Couldn't load file: " + filepath + ". Status code: " + response.status);
                return;
            }
            response.text().then(function (data) {
                var lines = data.split(/\r?\n/);
                for (var line = 0; line < lines.length; line++) {
                    processFileLine(lines[line]);
                }
                init();
            })
        })
        .catch(function (err) {
            console.log('Fetch Error :-S', err);
        });
}

function processFileLine(line)
{
    if (line.startsWith("#") || (!line || 0 === line.length) || line.startsWith("Accession") || line.startsWith('\t'))
    {
        return;
    }
    
    var tokens = line.split('\t');
    var lineName = tokens[0];
    lineNames.push(lineName);
    lineData.set(lineName, tokens.slice(1).map(getState));
}

function getState(allele)
{
    if (allele === '-' || (!allele || 0 === allele.length))
        allele = '';

    if (!stateTable.has(allele))
    {
        stateTable.set(allele, stateTable.size);
    }

    return stateTable.get(allele);
}

function init()
{
    // var controls = document.getElementById('controls');
    var height = window.innerHeight - document.getElementById("canvas-holder").offsetTop;

    font = calculateFontSize('C/G', 'sans-serif');

    // Set up the canvas and drawing context for the genotype display
    canvas = document.createElement('canvas');
    canvas.id = 'genotype';
    ctx = canvas.getContext('2d');

    ctx.font = font;

    document.getElementById('canvas-holder').appendChild(canvas);
    canvas.width = document.getElementById('canvas-holder').scrollWidth;
    canvas.height = height;// - controls.offsetHeight;
   // console.log("canvas height: " + canvas.height + " controls height: " + controls.offsetHeight + " height: " + height);

    // Pre-render our gradient squares
    setupColorStamps();

    // Add event handlers for mouse events to allow movement of the displays
    canvas.addEventListener('mousedown', onmousedown, false);
    window.addEventListener('mouseup', onmouseup, false);
    window.addEventListener('mousemove', onmousemove, false);

    render();
}

function calculateFontSize(text, fontface)
{
    var fontCanvas = document.createElement('canvas');
    fontCanvas.width = boxSize;
    fontCanvas.height = boxSize;
    var fontContext = fontCanvas.getContext('2d');
    
    fontSize = 100;
    fontContext.font = fontSize + "px " + fontface;

    while (fontContext.measureText(text).width > fontCanvas.width)
    {
        fontSize--;
        fontContext.font = fontSize + "px " + fontface;
    }

    return fontContext.font;
}

// Generates a set of homozygous and heterozygous color stamps from the stateTable
function setupColorStamps()
{
    colorStamps = [];
    for (var key of stateTable.keys())
    {
        if (key.length <= 1)
        {
            // If we fail to find a key for whatever reason, get the blank stamp
            var nucleotide = nucleotides.get(key);
            if (nucleotide === undefined)
            {
                nucleotide = nucleotides.get('');
            }
            var stamp = new HomozygousColorState(nucleotide)
            colorStamps.push(stamp);
        }
        else
        {
            var alleles = key.split('/');
            var nucleotide1 = nucleotides.get(alleles[0]);
            var nucleotide2 = nucleotides.get(alleles[1]);
            var stamp = new HeterozygousColorState(nucleotide1, nucleotide2);
            colorStamps.push(stamp);
        }
    }
}

function render()
{
    lineStart = Math.floor(translatedY / boxSize);
    lineEnd = Math.min(lineStart + (canvas.height/boxSize), lineNames.length);

    var totalAlleles = lineData.get(lineNames[0]).length;

    var alleleStart = Math.floor(translatedX / boxSize);
    var alleleEnd = Math.min(alleleStart + (canvas.width/boxSize), totalAlleles);

    renderGermplasm(lineNames, lineStart, lineEnd, alleleStart, alleleEnd);
}

function renderGermplasm(lineNames, lineStart, lineEnd, alleleStart, alleleEnd)
{
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    var lineCount = 0;
    for (var i=lineStart; i < lineEnd; i++)
    {
        ctx.fillText(lineNames[i], 0, (lineCount * boxSize) + (boxSize - (fontSize/2)));
        lineCount++;
    }

    var currentLine = 0;
    for (var i = lineStart; i < lineEnd; i++)
    {
        var alleles = lineData.get(lineNames[i]);
        var currentAllele = 0;
        for (var j=alleleStart; j < alleleEnd; j++)
        {
            ctx.drawImage(colorStamps[alleles[j]].buffer, 100 + (currentAllele * boxSize), currentLine * boxSize);
            currentAllele++;
        }
        currentLine++;
    }
}

function onmousedown(ev)
{
    var e = ev || event;
    dragStartX = e.pageX;
    dragStartY = e.pageY;
    dragging = true;
}

function onmouseup(ev)
{
    dragging = false;
}

function onmousemove(ev)
{
    var e = ev || event;
    if(dragging)
    {
        var diffX = e.pageX - dragStartX;
        translatedX -= diffX;
        var diffY = e.pageY - dragStartY;
        translatedY -= diffY;
        dragStartX = e.pageX;
        dragStartY = e.pageY;

        if (translatedX < 0)
            translatedX = 0;
        if (translatedY < 0)
            translatedY = 0;

        render();
    }
}

function drawGradientSquare(gradCanvas, color1, color2, text)
{
    gradCanvas.width = boxSize;
    gradCanvas.height = boxSize;
    var gradientCtx = gradCanvas.getContext('2d');

    var lingrad = gradientCtx.createLinearGradient(0, 0, boxSize, boxSize);
    lingrad.addColorStop(0, color1);
    lingrad.addColorStop(1, color2);
    gradientCtx.fillStyle = lingrad;
    gradientCtx.fillRect(0, 0, boxSize, boxSize);

    if (boxSize >= 10)
    {
        gradientCtx.fillStyle = 'rgb(0,0,0)';
        gradientCtx.font = font;
        var textWidth = gradientCtx.measureText(text).width
        gradientCtx.fillText(text, (boxSize-textWidth)/2, (boxSize-(fontSize/2)));
    }
}

function drawHetSquare(gradCanvas, color1, color2, color3, color4, text)
{
    gradCanvas.width = boxSize;
    gradCanvas.height = boxSize;
    var gradientCtx = gradCanvas.getContext('2d');

    var lingrad = gradientCtx.createLinearGradient(0, 0, boxSize, boxSize);
    lingrad.addColorStop(0, color1);
    lingrad.addColorStop(1, color2);
    gradientCtx.fillStyle = lingrad;
    gradientCtx.beginPath();
    gradientCtx.lineTo(boxSize, 0);
    gradientCtx.lineTo(0, boxSize);
    gradientCtx.lineTo(0, 0);
    gradientCtx.fill();

    var lingrad2 = gradientCtx.createLinearGradient(0, 0, boxSize, boxSize);
    lingrad2.addColorStop(0, color3);
    lingrad2.addColorStop(1, color4);
    gradientCtx.fillStyle = lingrad2;
    gradientCtx.beginPath();
    gradientCtx.moveTo(boxSize, 0);
    gradientCtx.lineTo(boxSize, boxSize);
    gradientCtx.lineTo(0, boxSize);
    gradientCtx.lineTo(boxSize, 0);
    gradientCtx.fill();

    if (boxSize >= 10)
    {
        gradientCtx.fillStyle = 'rgb(0,0,0)';
        gradientCtx.font = font;
        var textWidth = gradientCtx.measureText(text).width
        gradientCtx.fillText(text, (boxSize-textWidth)/2, (boxSize-(fontSize/2)));
    }
}

function zoom(size)
{
    boxSize = size;
    font = calculateFontSize('C/G', 'sans-serif');
    ctx.font = font;

    console.log("boxSize: " + boxSize + " fontSize: " + fontSize);


    setupColorStamps();
    render();
}