function navbar() {
    var x = document.getElementById("myTopnav");
    if (x.className === "topnav") {
        x.className += " responsive";
    } else {
        x.className = "topnav";
    }
}

username = document.querySelector('.dropbtn').textContent.trim();

<!--
function displayUnreads(username){
    fetch('unreads/'+username,{
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {

        })
}
-->
