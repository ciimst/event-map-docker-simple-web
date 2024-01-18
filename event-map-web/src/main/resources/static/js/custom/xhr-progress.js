const handleProgress = (e) => {
    let contentLength;
    if (e.lengthComputable) {
        contentLength = e.total;
    } else {
        contentLength = parseInt(e.target.getResponseHeader('Content-Length'), 10);
    }
    document.querySelector("#modalProgressBar").style.width = (((e.loaded / contentLength) * 100)|0) + "%";
    document.querySelector("#modalProgressBar").innerHTML = `${(((e.loaded / contentLength) * 100)|0) + "%"}`;
    if (e.loaded === contentLength) {
        document.querySelector("#progressDialog").style.display = "none";
    }

}

const XhrProgress = (url, callback) => {
    const xhr = new XMLHttpRequest();
    xhr.addEventListener('progress', handleProgress);
    xhr.open("GET", url);
    xhr.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
    xhr.onreadystatechange = (e) => {
        if (xhr.readyState === 4 && xhr.status === 200) { // 4 done demek
            if (typeof callback === "function") {
                callback.apply(xhr);
            }
        }
    };
    document.querySelector("#timeDimensionSpinner").style.display = "none"
    document.querySelector("#progressDialog").style.display = "block";
    xhr.send();

};