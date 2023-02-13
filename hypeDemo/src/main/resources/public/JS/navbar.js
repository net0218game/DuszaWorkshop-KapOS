function navbar() {
    var x = document.getElementById("myTopnav");
    if (x.className === "topnav") {
        x.className += " responsive";
    } else {
        x.className = "topnav";
    }
}

var username = document.getElementById('username').innerText;
var unreadBin = document.querySelector('#unread-bin');
/*
function displayUnreads(){
    fetch('/unreads/'+username,{
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {
            console.log(data)
            for (let j=0;j<Object.keys(data);j++) {
                var fodiv = document.createElement('div')
                fodiv.className = 'dropdown-content unread-message'

                var hr = document.createElement('hr')

                var i = document.createElement('i')
                i.style.backgound = 'red'
                i.innerText = data[j].id

                var aldiv = document.createElement('div')

                var span = document.createElement('span')
                span.innerText = data[j].sender

                var p = document.createElement('p')
                var text = date[j].content
                if (text.length > 20) {
                    text = text.substring(20) + '...'
                }
                p.innerText = text
                aldiv.appendChild(span)
                aldiv.appendChild(p)

                fodiv.appendChild(hr)
                fodiv.appendChild(i)
                fodiv.appendChild(aldiv)

                unreadBin.appendChild(fodiv)
            }
        })
}

displayUnreads()
*/