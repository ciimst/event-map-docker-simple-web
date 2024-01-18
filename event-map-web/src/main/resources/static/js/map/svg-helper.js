var SvgIcon = {    
    PageKod: "Svg Icon",    
    GenerateBase64Icon : function (image, color){
    	
    	var svgBodyIconSettings = {
			mapIconUrl: '<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" width="100px" height="100px" viewBox="0 0 100 100" enable-background="new 0 0 100 100" xml:space="preserve">' + 
						'<circle id="lp" fill="{selectionColor}" cx="49.978" cy="50.008" r="49.995"/>' + 
						'<linearGradient id="SVGID_1_" gradientUnits="userSpaceOnUse" x1="49.9995" y1="8.5" x2="0" y2="91.5005">' + 
						'<stop  offset="0" style="stop-color:{iconColorStart}"/><stop  offset="1" style="stop-color:{iconColorEnd}"/></linearGradient>' + 
						'<circle fill="url(#SVGID_1_)" cx="50" cy="50" r="41.5"/><g>' +
						'{content} </g></svg>',
			content: image,
			iconColorStart: color,				
			iconColorEnd: 'black',
			selectionColor: 'none',
			
		};
    	
        var base64Icon = "data:image/svg+xml;base64," + btoa(L.Util.template(svgBodyIconSettings.mapIconUrl, svgBodyIconSettings));
        return base64Icon;
    },
}

