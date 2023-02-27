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


/*
light
dark
blue
purple
lightblue*/