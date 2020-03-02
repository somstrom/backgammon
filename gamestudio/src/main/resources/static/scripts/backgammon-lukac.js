dices();
function dices(){

    var elementTakenAway = document.getElementById('taken-away');
    var elementDices = document.getElementById('dices');
    var elementDices2 = document.getElementById('dices2');

    if(elementDices != null) var countOfDices = elementDices.rows[0].cells.length;
    if(elementDices2 != null) var countOfDices2 = elementDices2.rows[0].cells.length;

    //margin without dices
    elementTakenAway.style.marginLeft = "287px";

    //margin with dices
    if (countOfDices !== 0) elementTakenAway.style.marginLeft = 37+(5-countOfDices)*20+ "px";

    if(elementDices !== null) elementDices.style.marginLeft = 50 + (5 - countOfDices) * 20 + "px";
    if(elementDices2 !== null) elementDices2.style.marginLeft = 30 + (5 - countOfDices2) * 20 + "px";

}

//http://codepen.io/pen/RRPQEX

// // The JavaScript (should work in all major browsers and IE8+)
//
// var elements = document.getElementsByClassName('element');
// var target = document.getElementsByClassName('target')[0];
// var button = document.getElementById('button');
//
// // store the x,y coordinates of the target
// var xT = target.offsetLeft;
// var yT = target.offsetTop;
//
// // add a click event listener to the button
// button.addEventListener('click', function() {
//     for (var i = 0; i < elements.length; i++) {
//         // store the elements coordinate
//         var xE = elements[i].offsetLeft;
//         var yE = elements[i].offsetTop;
//         // set elements position to their position for smooth animation
//         elements[i].style.left = xE + 'px';
//         elements[i].style.top = yE + 'px';
//         // set their position to the target position
//         // the animation is a simple css transition
//         elements[i].style.left = xT + 'px';
//         elements[i].style.top = yT + 'px';
//     }
// });

// function moveTrinket(start,end,i) {
//     var trinketsLost = document.getElementById(end);
//     var trinketsHeld = document.getElementById(start);
//     var trinketOrig = document.getElementById(i);
//
//     // clone the element (wee need the clone for positioning)
//     var trinketClone = trinketOrig.cloneNode();
//     trinketClone.style.visibility = 'hidden';
//     trinketsLost.appendChild(trinketClone);
//
//     // calculate the new position, relative to the current position
//     var trinketOrigTop = trinketOrig.getBoundingClientRect().top;
//     var trinketOrigLeft = trinketOrig.getBoundingClientRect().left;
//     var trinketCloneTop = trinketClone.getBoundingClientRect().top;
//     var trinketCloneLeft = trinketClone.getBoundingClientRect().left;
//     var newPositionTop = (trinketCloneTop - trinketOrigTop);
//     var newPositionLeft = (trinketCloneLeft - trinketOrigLeft);
//
//     // remove the clone (we do not need it anymore)
//     trinketClone.parentNode.removeChild(trinketClone);
//
//     // create a placeholder to prevent other elements from changing their position
//     var placeholder = document.createElement('div');
//     placeholder.classList.add('trinket-placeholder');
//     trinketOrig.parentNode.insertBefore(placeholder, trinketOrig.nextSibling);
//
//     // position the original at the clone's position (this triggers the transition)
//     trinketOrig.style.zIndex = 1000;
//     trinketOrig.style.top = newPositionTop + 'px';
//     trinketOrig.style.left = newPositionLeft + 'px';
//
//     // this will be triggered after the transition finished
//     trinketOrig.addEventListener('transitionend', function() {
//         // reset the positioning
//         this.style.position = 'scroll';
//         this.style.top = 0;
//         this.style.left = 0;
//
//         // shrink the placeholder to re-center the held trinkets
//         placeholder.style.width = 0;
//         placeholder.style.marginLeft = 0;
//
//         // when the placeholder transition has finished, remove the placeholder
//         placeholder.addEventListener('transitionend', function (){
//             this.parentnode.removeChild(this);
//
//             // removing the placeholder is the last action,
//             // after that you can do any following actions
//             roundreset();
//         });
//
//         // move the trinket element in the DOM (from held to lost)
//         trinketsLost.appendChild(this);
//     });
// }