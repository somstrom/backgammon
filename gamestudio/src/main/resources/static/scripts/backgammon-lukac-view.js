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
    if (countOfDices !== 0) elementTakenAway.style.marginLeft = 55+(5-countOfDices)*30+ "px";

    if(elementDices !== null) elementDices.style.marginLeft = 75 + (5 - countOfDices) * 30 + "px";
    if(elementDices2 !== null) elementDices2.style.marginLeft = 45 + (5 - countOfDices2) * 30 + "px";

}