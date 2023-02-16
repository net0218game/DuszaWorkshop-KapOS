function navbar() {
    var x = document.getElementById("myTopnav");
    if (x.className === "topnav") {
        x.className += " responsive";
    } else {
        x.className = "topnav";
    }
}

var username = document.getElementById('user').innerText;
var unreadBin = document.getElementById('unreadMessages');
var unreadCount = document.getElementById('sumUnread')

function displayUnreads() {
    fetch('/unreads/' + username, {
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {
            console.log(data)
            var osszUnread = 0

            if (Object.keys(data).length === 0){
                var zeroUnread = document.createElement('p')
                zeroUnread.classList.add('unread-empty')
                unreadBin.appendChild(zeroUnread)

            }else{
                for (let j = 0; j < Object.keys(data).length; j++) {
                    console.log('lefutott')
                    var hr = document.createElement('hr')

                    var i = document.createElement('i')
                    i.style.backgroundColor = 'red'
                    i.innerText = data[j].id
                    osszUnread += data[j].id

                    var aldiv = document.createElement('div')

                    var span = document.createElement('span')
                    span.innerText = data[j].sender

                    var p = document.createElement('p')
                    var text = data[j].content
                    if (text.length > 20) {
                        text = text.substring(20) + '...'
                    }
                    p.innerText = text
                    aldiv.appendChild(span)
                    aldiv.appendChild(p)
                    aldiv.classList.add('unread-info')

                    unreadBin.appendChild(hr)
                    unreadBin.appendChild(i)
                    unreadBin.appendChild(aldiv)
                }
                unreadCount.innerText = osszUnread
            }

        })
}

displayUnreads()