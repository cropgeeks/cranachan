(function (global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) :
  typeof define === 'function' && define.amd ? define(['exports'], factory) :
  (factory((global.GenotypeRenderer = {})));
}(this, (function (exports) { 'use strict';

  function _classCallCheck(instance, Constructor) {
    if (!(instance instanceof Constructor)) {
      throw new TypeError("Cannot call a class as a function");
    }
  }

  function _defineProperties(target, props) {
    for (var i = 0; i < props.length; i++) {
      var descriptor = props[i];
      descriptor.enumerable = descriptor.enumerable || false;
      descriptor.configurable = true;
      if ("value" in descriptor) descriptor.writable = true;
      Object.defineProperty(target, descriptor.key, descriptor);
    }
  }

  function _createClass(Constructor, protoProps, staticProps) {
    if (protoProps) _defineProperties(Constructor.prototype, protoProps);
    if (staticProps) _defineProperties(Constructor, staticProps);
    return Constructor;
  }

  var Nucleotide = function Nucleotide(allele, colorLight, colorDark) {
    _classCallCheck(this, Nucleotide);

    this.allele = allele;
    this.colorLight = colorLight;
    this.colorDark = colorDark;
  };

  var Marker = function Marker(name, chromosome, position) {
    _classCallCheck(this, Marker);

    this.name = name;
    this.chromosome = chromosome;
    this.position = position;
  };

  var ColorState = function ColorState(buffer) {
    _classCallCheck(this, ColorState);

    this.buffer = buffer;
  };

  var ScrollBar =
  /*#__PURE__*/
  function () {
    function ScrollBar(parentWidth, parentHeight, width, height, vertical) {
      _classCallCheck(this, ScrollBar);

      this.parentWidth = parentWidth;
      this.parentHeight = parentHeight;
      this.width = width;
      this.height = height;
      this.vertical = vertical;
      this.x = vertical ? parentWidth - width : 0;
      this.y = vertical ? 0 : parentHeight - height;
      this.widget = new ScrollBarWidget(this.x, this.y, this.vertical ? this.width : 20, this.vertical ? 20 : this.height);
    }

    _createClass(ScrollBar, [{
      key: "render",
      value: function render(ctx) {
        ctx.fillStyle = '#eee';
        ctx.strokeStyle = '#eee';
        ctx.fillRect(this.x, this.y, this.width, this.height);
        this.widget.render(ctx);
      }
    }, {
      key: "move",
      value: function move(x, y) {
        this.widget.move(x, y);
      }
    }]);

    return ScrollBar;
  }();

  var ScrollBarWidget =
  /*#__PURE__*/
  function () {
    function ScrollBarWidget(x, y, width, height) {
      _classCallCheck(this, ScrollBarWidget);

      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.corner_radius = 5;
    }

    _createClass(ScrollBarWidget, [{
      key: "render",
      value: function render(ctx) {
        // Set faux rounded corners
        ctx.lineJoin = "round";
        ctx.lineWidth = this.corner_radius;
        ctx.fillStyle = '#aaa';
        ctx.strokeStyle = '#aaa'; // Change origin and dimensions to match true size (a stroke makes the shape a bit larger)

        ctx.strokeRect(this.x + this.corner_radius / 2, this.y + this.corner_radius / 2, this.width - this.corner_radius, this.height - this.corner_radius);
        ctx.fillRect(this.x + this.corner_radius / 2, this.y + this.corner_radius / 2, this.width - this.corner_radius, this.height - this.corner_radius);
      }
    }, {
      key: "move",
      value: function move(x, y) {
        this.x = x;
        this.y = y;
      }
    }]);

    return ScrollBarWidget;
  }();

  function GenotypeRenderer() {
    var genotypeRenderer = {};
    var brapiJs; // Variables for referring to the genotype canvas

    var backCanvas;
    var backCtx;
    var canvas;
    var ctx; // Mouse related variables

    var dragStartX = null;
    var dragStartY = null;
    var dragging = false;
    var translatedX = 0;
    var translatedY = 0; // Variables to keep track of where we are in the data

    var lineStart = 0;
    var lineEnd = 0;
    var boxSize = 16;
    var maxCanvasHeight;
    var maxCanvasWidth;
    var verticalScrollbar;
    var horizontalScrollbar;
    var fontSize = 100;
    var lineNamesWidth = 100;
    var mapCanvasHeight = 30;
    var alleleCanvasWidth;
    var alleleCanvasHeight;
    var stateTable = new Map();
    var lineNames = [];
    var lineData = [];
    var markerData = [];
    var colorStamps = [];
    var redraw = true;
    var lineUnderMouse;
    var markerUnderMouse;
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

    genotypeRenderer.renderGenotypesBrapi = function (domParent, width, height, server, matrixId) {
      mapCanvasHeight = 0; // createRendererComponents(domParent, width, height);

      brapiJs = BrAPI(server);
      var params = {
        "matrixDbId": [matrixId],
        "format": 'flapjack'
      };
      brapiJs.allelematrices_search(params).each(function (matrixObject) {
        // var myInit = {
        //   method: 'GET',
        //   headers: {
        //     'Content-Type': 'text/tsv'
        //   },
        //   mode: 'cors',
        //   cache: 'default'
        // };
        renderGenotypesUrl(domParent, width, height, undefined, matrixObject.__response.metadata.datafiles[0]);
      });
      return genotypeRenderer;
    };

    genotypeRenderer.renderGenotypesUrl = function (domParent, width, height, mapFileURL, genotypeFileURL) {
      createRendererComponents(domParent, width, height);
      fetch(mapFileURL).then(function (response) {
        if (response.status !== 200) {
          console.log("Couldn't load file: " + filepath + ". Status code: " + response.status);
          return;
        }

        response.text().then(function (data) {
          var lines = data.split(/\r?\n/);

          for (var line = 0; line < lines.length; line++) {
            processMapFileLine(lines[line]);
          }
        });
      }).catch(function (err) {
        console.log('Fetch Error :-S', err);
      });
      fetch(genotypeFileURL).then(function (response) {
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
        });
      }).catch(function (err) {
        console.log('Fetch Error :-S', err);
      });
      return genotypeRenderer;
    };

    genotypeRenderer.renderGenotypesFile = function (domParent, width, height, mapFileDom, genotypeFileDom) {
      createRendererComponents(domParent, width, height);
      loadMapData(mapFileDom);
      loadGenotypeData(genotypeFileDom);
      return genotypeRenderer;
    };

    function createRendererComponents(domParent, width, height) {
      var canvasHolder = document.getElementById(domParent.slice(1)); // Set up the canvas and drawing context for the genotype display

      canvas = document.createElement('canvas');
      canvas.id = 'genotype';
      canvas.width = width;
      maxCanvasWidth = width;
      canvas.height = height;
      maxCanvasHeight = height;
      ctx = canvas.getContext('2d');
      canvasHolder.append(canvas);
      backCanvas = document.createElement('canvas');
      backCanvas.width = width;
      backCanvas.height = height;
      backCtx = backCanvas.getContext('2d');
      verticalScrollbar = new ScrollBar(canvas.width, canvas.height - mapCanvasHeight - 10, 10, canvas.height - mapCanvasHeight - 10, true);
      horizontalScrollbar = new ScrollBar(canvas.width - lineNamesWidth - 10 - 1, canvas.height, canvas.width - lineNamesWidth - 10 - 1, 10, false);
      var zoomDiv = document.createElement('div');
      zoomDiv.id = 'zoom-holder';
      var zoomLabel = document.createElement('label');
      zoomLabel.setAttribute('for', 'zoom-control');
      zoomLabel.innerHTML = "Zoom:";
      var range = document.createElement('input');
      range.setAttribute('type', 'range');
      range.min = 2;
      range.max = 64;
      range.value = 16;
      range.addEventListener('change', function () {
        zoom(range.value);
      });
      range.addEventListener('input', function () {
        zoom(range.value);
      });
      zoomDiv.appendChild(zoomLabel);
      zoomDiv.appendChild(range);
      canvasHolder.appendChild(zoomDiv);
    }

    function loadMapData(map_file_dom) {
      var file = document.getElementById(map_file_dom.slice(1)).files[0];
      var reader = new FileReader();

      reader.onloadend = function () {
        var markers = this.result.split(/\r?\n/);

        for (var marker = 0; marker < markers.length; marker++) {
          processMapFileLine(markers[marker]);
        }
      };

      reader.readAsText(file);
    }

    function processMapFileLine(line) {
      if (line.startsWith("#") || !line || 0 === line.length || line.startsWith('\t')) {
        return;
      }

      var tokens = line.split('\t');

      if (tokens.length === 2) {
        return;
      }

      var markerName = tokens[0];
      var marker = new Marker(markerName, tokens[1], tokens[2]);
      markerData.push(marker);
    }

    function loadGenotypeData(genotype_file_dom) {
      var file = document.getElementById(genotype_file_dom.slice(1)).files[0];
      var reader = new FileReader();

      reader.onloadend = function () {
        var lines = this.result.split(/\r?\n/);

        for (var line = 0; line < lines.length; line++) {
          processFileLine(lines[line]);
        }

        init();
      };

      reader.readAsText(file);
    }

    function processFileLine(line) {
      if (line.startsWith("#") || !line || 0 === line.length || line.startsWith("Accession") || line.startsWith('\t')) {
        return;
      }

      var tokens = line.split('\t');
      var lineName = tokens[0];
      lineNames.push(lineName);
      lineData.push(tokens.slice(1).map(getState));
    }

    function getState(allele) {
      if (allele === '-' || !allele || 0 === allele.length) allele = '';

      if (!stateTable.has(allele)) {
        stateTable.set(allele, stateTable.size);
      }

      return stateTable.get(allele);
    }

    function init() {
      // Pre-render our gradient squares
      setupColorStamps(); // Add event handlers for mouse events to allow movement of the displays

      canvas.addEventListener('mousedown', onmousedown, false);
      window.addEventListener('mouseup', onmouseup, false);
      window.addEventListener('mousemove', onmousemove, false);
      canvas.addEventListener('mousemove', overlay_listener, false);
      canvas.addEventListener('mouseleave', overlay_leave, false);
      render();
    }

    function calculateFontSize(text, fontface, size) {
      var fontCanvas = document.createElement('canvas');
      fontCanvas.width = size;
      fontCanvas.height = size;
      var fontContext = fontCanvas.getContext('2d');
      fontSize = 100;
      fontContext.font = fontSize + "px " + fontface;

      while (fontContext.measureText(text).width > fontCanvas.width) {
        fontSize--;
        fontContext.font = fontSize + "px " + fontface;
      }

      backCtx.font = fontContext.font;
      return fontContext.font;
    } // Generates a set of homozygous and heterozygous color stamps from the stateTable


    function setupColorStamps() {
      colorStamps = [];
      var _iteratorNormalCompletion = true;
      var _didIteratorError = false;
      var _iteratorError = undefined;

      try {
        for (var _iterator = stateTable.keys()[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
          var key = _step.value;

          if (key.length <= 1) {
            // If we fail to find a key for whatever reason, get the blank stamp
            var nucleotide = nucleotides.get(key);

            if (nucleotide === undefined) {
              nucleotide = nucleotides.get('');
            }

            var buffer = drawGradientSquare(boxSize, nucleotide);
            var stamp = new ColorState(buffer);
            colorStamps.push(stamp);
          } else {
            var alleles = key.split('/');
            var nucleotide1 = nucleotides.get(alleles[0]);
            var nucleotide2 = nucleotides.get(alleles[1]);

            var _buffer = drawHetSquare(boxSize, nucleotide1, nucleotide2);

            var _stamp = new ColorState(_buffer);

            colorStamps.push(_stamp);
          }
        }
      } catch (err) {
        _didIteratorError = true;
        _iteratorError = err;
      } finally {
        try {
          if (!_iteratorNormalCompletion && _iterator.return != null) {
            _iterator.return();
          }
        } finally {
          if (_didIteratorError) {
            throw _iteratorError;
          }
        }
      }
    }

    function render() {
      lineStart = Math.floor(translatedY / boxSize);
      lineEnd = Math.min(lineStart + Math.floor(canvas.height / boxSize), lineNames.length);
      var totalAlleles = lineData[0].length - 1;
      maxCanvasWidth = totalAlleles * boxSize;
      maxCanvasHeight = lineNames.length * boxSize;
      alleleCanvasWidth = canvas.width - lineNamesWidth;
      alleleCanvasHeight = canvas.height - mapCanvasHeight;
      var alleleStart = Math.floor(translatedX / boxSize);
      var alleleEnd = Math.min(alleleStart + Math.floor(alleleCanvasWidth / boxSize), totalAlleles);
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      if (redraw) {
        backCtx.clearRect(0, 0, canvas.width, canvas.height);
        renderMap(alleleStart, alleleEnd);
        renderGermplasmNames(lineNames, lineStart, lineEnd);
        renderGermplasm(lineStart, lineEnd, alleleStart, alleleEnd);
        renderScrollbars();
      }

      ctx.drawImage(backCanvas, 0, 0);

      if (lineUnderMouse && markerUnderMouse) {
        ctx.translate(lineNamesWidth, mapCanvasHeight);
        ctx.globalAlpha = 0.4;
        ctx.fillStyle = "#fff";
        ctx.fillRect(markerUnderMouse * boxSize, 0, boxSize, alleleCanvasHeight);
        ctx.fillRect(0, lineUnderMouse * boxSize, alleleCanvasWidth, boxSize);
        ctx.translate(-lineNamesWidth, -mapCanvasHeight);
        ctx.globalAlpha = 1;
      }

      redraw = false;
    }

    function renderMap(alleleStart, alleleEnd) {
      if (markerData.length == 0) {
        mapCanvasHeight = 0;
        return;
      }

      var firstMarkerPos = markerData[alleleStart].position;
      var lastMarkerPos = markerData[alleleEnd].position;
      var dist = lastMarkerPos - firstMarkerPos;
      backCtx.lineWidth = 1;
      backCtx.strokeStyle = 'gray';
      backCtx.translate(lineNamesWidth, 0);

      for (var i = alleleStart; i < alleleEnd; i++) {
        var pos = (i - alleleStart) * boxSize;
        pos += boxSize / 2;
        var marker = markerData[i];
        var markerPos = (marker.position - firstMarkerPos) * (alleleCanvasWidth / dist);
        backCtx.beginPath();
        backCtx.moveTo(markerPos, 0);
        backCtx.lineTo(pos, 20);
        backCtx.lineTo(pos, mapCanvasHeight);
        backCtx.stroke();
      }

      backCtx.translate(-lineNamesWidth, 0);
    }

    function renderScrollbars() {
      backCtx.translate(0, mapCanvasHeight);
      verticalScrollbar.render(backCtx);
      backCtx.translate(0, -mapCanvasHeight);
      backCtx.translate(lineNamesWidth, 0);
      horizontalScrollbar.render(backCtx);
      backCtx.translate(-lineNamesWidth, 0);
      backCtx.translate(lineNamesWidth, mapCanvasHeight);
      backCtx.fillStyle = "#aaa";
      backCtx.strokeRect(alleleCanvasWidth - 10, alleleCanvasHeight - 10, 10, 10);
      backCtx.fillRect(alleleCanvasWidth - 10, alleleCanvasHeight - 10, 10, 10);
      backCtx.translate(-lineNamesWidth, -mapCanvasHeight);
    }

    function renderGermplasmNames(lineNames, lineStart, lineEnd) {
      backCtx.fillStyle = '#333';
      backCtx.translate(0, mapCanvasHeight);
      var lineCount = 0;

      for (var i = lineStart; i < lineEnd; i++) {
        backCtx.fillText(lineNames[i], 0, lineCount * boxSize + (boxSize - fontSize / 2));
        lineCount++;
      }

      backCtx.translate(0, -mapCanvasHeight);
    }

    function renderGermplasm(lineStart, lineEnd, alleleStart, alleleEnd) {
      backCtx.translate(lineNamesWidth, mapCanvasHeight);
      var currentLine = 0;

      for (var i = lineStart; i < lineEnd; i++) {
        var alleles = lineData[i];
        var currentAllele = 0;

        for (var j = alleleStart; j < alleleEnd; j++) {
          backCtx.drawImage(colorStamps[alleles[j]].buffer, currentAllele * boxSize, currentLine * boxSize);
          currentAllele++;
        }

        currentLine++;
      }

      backCtx.translate(-lineNamesWidth, -mapCanvasHeight);
    }

    function onmousedown(ev) {
      var e = ev || event;
      dragStartX = e.pageX;
      dragStartY = e.pageY;
      dragging = true;
    }

    function onmouseup() {
      dragging = false;
    }

    function onmousemove(ev) {
      var e = ev || event;

      if (dragging) {
        var diffX = e.pageX - dragStartX;
        translatedX -= diffX;
        var diffY = e.pageY - dragStartY;
        translatedY -= diffY;
        dragStartX = e.pageX;
        dragStartY = e.pageY;
        if (translatedX < 0) translatedX = 0;
        if (translatedY < 0) translatedY = 0;
        if (translatedX / boxSize >= maxCanvasWidth / boxSize - alleleCanvasWidth / boxSize) translatedX = maxCanvasWidth - alleleCanvasWidth;
        if (translatedY / boxSize >= maxCanvasHeight / boxSize - alleleCanvasHeight / boxSize) translatedY = maxCanvasHeight - alleleCanvasHeight;
        var scrollHeight = alleleCanvasHeight - 10 - 20;
        var scrollWidth = alleleCanvasWidth - 10 - 20;
        var scrollX = Math.floor(map(translatedX, 0, maxCanvasWidth - alleleCanvasWidth, 0, scrollWidth));
        var scrollY = Math.floor(map(translatedY, 0, maxCanvasHeight - alleleCanvasHeight, 0, scrollHeight));
        verticalScrollbar.move(verticalScrollbar.x, scrollY);
        horizontalScrollbar.move(scrollX, horizontalScrollbar.y);
        redraw = true;
        render();
      }
    }

    function overlay_listener(ev) {
      var e = ev || event;
      var rect = canvas.getBoundingClientRect();
      var x = (e.clientX - rect.left) / (rect.right - rect.left) * canvas.width;
      var y = (e.clientY - rect.top) / (rect.bottom - rect.top) * canvas.height;

      if (x >= lineNamesWidth && x < canvas.width && y >= mapCanvasHeight && y < canvas.height) {
        markerUnderMouse = Math.floor((x - lineNamesWidth) / boxSize);
        lineUnderMouse = Math.floor((y - mapCanvasHeight) / boxSize);
      } else {
        lineUnderMouse = markerUnderMouse = undefined;
      }

      render();
    }

    function overlay_leave(ev) {
      lineUnderMouse = markerUnderMouse = undefined;
      render();
    }

    function map(num, inMin, inMax, outMin, outMax) {
      return (num - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    function drawGradientSquare(boxSize, nucleotide) {
      var gradCanvas = document.createElement('canvas');
      gradCanvas.width = boxSize;
      gradCanvas.height = boxSize;
      var gradientCtx = gradCanvas.getContext('2d');
      var lingrad = gradientCtx.createLinearGradient(0, 0, boxSize, boxSize);
      lingrad.addColorStop(0, nucleotide.colorLight);
      lingrad.addColorStop(1, nucleotide.colorDark);
      gradientCtx.fillStyle = lingrad;
      gradientCtx.fillRect(0, 0, boxSize, boxSize);

      if (boxSize >= 10) {
        gradientCtx.fillStyle = 'rgb(0,0,0)';
        gradientCtx.font = calculateFontSize('C/G', 'sans-serif', boxSize);
        var textWidth = gradientCtx.measureText(nucleotide.allele).width;
        gradientCtx.fillText(nucleotide.allele, (boxSize - textWidth) / 2, boxSize - fontSize / 2);
      }

      return gradCanvas;
    }

    function drawHetSquare(boxSize, nucleotide1, nucleotide2) {
      var gradCanvas = document.createElement('canvas');
      gradCanvas.width = boxSize;
      gradCanvas.height = boxSize;
      var gradientCtx = gradCanvas.getContext('2d');
      var lingrad = gradientCtx.createLinearGradient(0, 0, boxSize, boxSize);
      lingrad.addColorStop(0, nucleotide1.colorLight);
      lingrad.addColorStop(1, nucleotide1.colorDark);
      gradientCtx.fillStyle = lingrad;
      gradientCtx.beginPath();
      gradientCtx.lineTo(boxSize, 0);
      gradientCtx.lineTo(0, boxSize);
      gradientCtx.lineTo(0, 0);
      gradientCtx.fill();
      var lingrad2 = gradientCtx.createLinearGradient(0, 0, boxSize, boxSize);
      lingrad2.addColorStop(0, nucleotide2.colorLight);
      lingrad2.addColorStop(1, nucleotide2.colorDark);
      gradientCtx.fillStyle = lingrad2;
      gradientCtx.beginPath();
      gradientCtx.moveTo(boxSize, 0);
      gradientCtx.lineTo(boxSize, boxSize);
      gradientCtx.lineTo(0, boxSize);
      gradientCtx.lineTo(boxSize, 0);
      gradientCtx.fill();

      if (boxSize >= 10) {
        gradientCtx.fillStyle = 'rgb(0,0,0)';
        gradientCtx.font = calculateFontSize('C/G', 'sans-serif', boxSize);
        var allele1Width = gradientCtx.measureText(nucleotide1.allele).width;
        gradientCtx.fillText(nucleotide1.allele, (boxSize / 2 - allele1Width) / 2, fontSize);
        var allele2Width = gradientCtx.measureText(nucleotide2.allele).width;
        gradientCtx.fillText(nucleotide2.allele, boxSize - (boxSize / 2 + allele2Width) / 2, boxSize - fontSize / 4);
      }

      return gradCanvas;
    }

    function zoom(size) {
      boxSize = size;
      setupColorStamps();
      redraw = true;
      render();
    }

    return genotypeRenderer;
  }

  exports.GenotypeRenderer = GenotypeRenderer;

  Object.defineProperty(exports, '__esModule', { value: true });

})));
