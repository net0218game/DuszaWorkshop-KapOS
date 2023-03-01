//const setTheme = theme=> document.documentElement.className= theme;

// function to set a given theme/color-scheme
function setTheme(themeName) {
    localStorage.setItem('theme', themeName);
    document.documentElement.className = themeName;
}

// Immediately invoked function to set the theme on initial load
(function () {
    if (localStorage.getItem('theme') === 'theme-dark') {
        setTheme('theme-dark');
    } else if (localStorage.getItem('theme') === 'theme-light') {
        setTheme('theme-light');
    } else if (localStorage.getItem('theme') === 'theme-blue') {
        setTheme('theme-blue');
    } else if (localStorage.getItem('theme') === 'theme-purple') {
        setTheme('theme-purple');
    } else if (localStorage.getItem('theme') === 'theme-lightblue') {
        setTheme('theme-lightblue');
    } else if (localStorage.getItem('theme') === 'theme-black') {
        setTheme('theme-black');
    } else if (localStorage.getItem('theme') === 'theme-sunset') {
        setTheme('theme-sunset');
    } else if (localStorage.getItem('theme') === 'theme-lime') {
        setTheme('theme-lime');
    } else if (localStorage.getItem('theme') === 'theme-sea') {
        setTheme('theme-sea');
    } else {
        setTheme('theme-black');
    }
})();

function light() {
    setTheme('theme-light');
}
function dark() {
    setTheme('theme-dark');
}
function blue() {
    setTheme('theme-blue');
}
function purple() {
    setTheme('theme-purple');
}
function lightblue() {
    setTheme('theme-lightblue');
}
function black() {
    setTheme('theme-black');
}
function sunset() {
    setTheme('theme-sunset');
}
function lime() {
    setTheme('theme-lime');
}
function sea() {
    setTheme('theme-sea');
}