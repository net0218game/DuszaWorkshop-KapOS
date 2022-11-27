const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry)=>{
        if(entry.isIntersecting){
            entry.target.classList.add('show');
        }else{
            entry.target.classList.remove('show');
        }
    });
});

const hiddenelements = document.querySelectorAll('.hidden');
hiddenelements.forEach((el) => (observer.observe(el)));

const hiddenelements2 = document.querySelectorAll('.hidden2');
hiddenelements2.forEach((el) => (observer.observe(el)));

const hiddenelements3 = document.querySelectorAll('.hidden3');
hiddenelements2.forEach((el) => (observer.observe(el)));