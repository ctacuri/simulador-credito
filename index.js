//async
//genetic

var Guy = function () {
  return {
    //genetic changes
    months: 0,//
    buyDespositRatio: 0,//
    isValid :1,
    //initial fixed data
    initialMonth: 1,
    buyMaxLoanMonths:240,
    savingInitialAmmount: 360000,//100000,//
    buyTotalAmmount: 650000,//
    savingMonthlyEarn: 6000, //
    buyMaxMonthlyPay: 4000, //
    extraEarn: [0,10000,0,0,0,0,8000,0,0,0,0,5000],
    buyMinimumInitial: 0.1,//
    buyTEA: 0.12, //
    depositTEA: 0.03, //
    rentAmmount: 0, // :: to review
    monthsToBuildingFinish: 10,//

    //calculated output
    buyInitialInvest: 0,//
    depositInitialInvest: 0,//
    depositMaxSaving: 0,//
    monthsToPay: 0,//
    buyMonthlyPay: 0,//
    buyFinalInterests: 0,
    buyFinalCost: 0,
    depositTotalSaving: 0,//
    rentTotalAmmount: 0,//

    update: updateGuy
  }
};


var Task = require('genetic').Task
  , options = {
    getRandomSolution: getRandomSolution
    , popSize: 250
    , stopCriteria: stopCriteria
    , fitness: fitness
    , minimize: false
    , mutateProbability: 0.05
    , mutate: mutate
    , crossoverProbability: 0.3
    , crossover: crossover
  };
//  , util = require('util');

function crossover(parent1, parent2, callback) {

  var child = new Guy();
  child.months = (Math.random() > 0.5) ? parent1.months : parent2.months;
  child.buyDespositRatio = (Math.random() > 0.5) ? parent1.buyDespositRatio : parent2.buyDespositRatio;
  child.update();

  callback(child)
}

function mutate(solution, callback) {
  if (Math.random() < 0.3) {
    solution.months = randomIntFromInterval(1, solution.buyMaxLoanMonths);
  }
  if (Math.random() < 0.3) {
    solution.buyDespositRatio = Math.random();
  }
  solution.update();
  callback(solution);
}

function getRandomSolution(callback) {
  let solution = new Guy()

  solution.months = randomIntFromInterval(1, solution.buyMaxLoanMonths);
  solution.buyDespositRatio = Math.random();

  solution.update();

  callback(solution);
}

function stopCriteria() {
  return (this.generation == 1000);
}

function fitness(solution, callback) {
  if (solution.buyFinalInterests == 0) {
    callback(0);
  } else {
    let xx = (Math.pow(1/solution.monthsToPay,2) + (1/solution.buyFinalInterests)) * solution.isValid;
    callback(xx);
  }
}

console.log('=== TEST BEGINS === ')
var
  t = new Task(options);
// t.on('run start', function () { console.log('run start'); util.log('run') })
// t.on('run finished', function (results) { console.log('run finished - ', results); util.log('run')})
// t.on('init start', function () { console.log('init start') })
// t.on('init end', function (pop) { console.log('init end', pop) })
// t.on('loop start', function () { console.log('loop start') })
// t.on('loop end', function () { console.log('loop end') })
// t.on('iteration start', function (generation) { console.log('iteration start - ',generation) })
// t.on('iteration end', function () { console.log('iteration end') })
// t.on('calcFitness start', function () { console.log('calcFitness start') })
//t.on('calcFitness end', function (pop) { console.log('calcFitness end', pop) })
// t.on('parent selection start', function () { console.log('parent selection start') })
// t.on('parent selection end', function (parents) { console.log('parent selection end ',parents) })
// t.on('reproduction start', function () { console.log('reproduction start') })
// 
// t.on('find sum', function () { console.log('find sum') })
// t.on('find sum end', function (sum) { console.log('find sum end', sum) })

// t.on('statistics', function (statistics) { console.log('statistics',statistics)})
// 
// t.on('normalize start', function () { console.log('normalize start') })
// t.on('normalize end', function (normalized) { console.log('normalize end',normalized) })
// t.on('child forming start', function () { console.log('child forming start') })
// t.on('child forming end', function (children) { console.log('child forming end',children) })
// t.on('child selection start', function () { console.log('child selection start') })
// t.on('child selection end', function (population) { console.log('child selection end',population) })
// 
// t.on('mutate', function () { console.log('MUTATION!') })
// 
// 
// t.on('reproduction end', function (children) { console.log('reproduction end',children) })
// 
t.on('error', function (error) { console.log('ERROR - ', error) });
t.run(function (stats) { 
  console.log('results', stats);

if (stats.max.isValid) {
  console.log('best', {
    months : stats.max.months,
    buyDespositRatio : stats.max.buyDespositRatio,


     buyInitialInvest: stats.max.buyInitialInvest,
     depositInitialInvest: stats.max.depositInitialInvest,
     monthsToPay: stats.max.monthsToPay,
     buyMonthlyPay: stats.max.buyMonthlyPay,
     buyFinalInterests: stats.max.buyFinalInterests,
     depositMaxSaving: stats.max.depositMaxSaving
     });
  } else {
    console.log('invalid result');
  }
});


////// LOAN CALCULATOR
function calcularCuota(tea, cuotas, monto) {
  let tem = (Math.pow(1.0 + (tea), 30 / 360.0) - 1.0);

  let x = Math.pow(1.0 + tem, cuotas);
  return monto * ((x * tem) / (x - 1.0));
}

function calcularInteresCuota(tea,saldo, dias) {
  return ((Math.pow(1.0 + tea, dias / 360.0) - 1.0) * saldo);
}

function randomIntFromInterval(min,max) {
    return Math.floor(Math.random()*(max-min+1)+min);
}

function updateGuy() {
      let _buyPendingToPay = 0, _renting = true;
      let x = this;

      //allow buy minumum by 
      if (x.buyMinimumInitial > 0  && x.buyMinimumInitial < 1) {
        x.buyMinimumInitial = x.buyMinimumInitial * x.buyTotalAmmount;
      }

      if (x.rentAmmount == 0) { x.buyDespositRatio = 1;}

     
      x.buyInitialInvest = Math.max(x.buyMinimumInitial, x.savingInitialAmmount * x.buyDespositRatio);
      x.depositInitialInvest = x.savingInitialAmmount - x.buyInitialInvest;
      x.depositTotalSaving = x.depositInitialInvest;

      _buyPendingToPay = x.buyTotalAmmount - x.buyInitialInvest;
      x.buyMonthlyPay = calcularCuota(x.buyTEA, x.months, _buyPendingToPay);// 
      
      x.buyFinalCost = x.buyInitialInvest;

      if ( (x.buyMonthlyPay + x.rentAmmount) > x.buyMaxMonthlyPay ) {
        x.isValid = 0;
        return;
      }

      let currentMonth = x.initialMonth-1;
      //runs for months
      for (let i = 1; i <= x.months; i++) {

        let _monthMoney = x.savingMonthlyEarn + x.extraEarn[currentMonth];

        //stop renting
        if (i > x.monthsToBuildingFinish) {
          _renting = false;
        }


        if (_renting) {
          //rent
          _monthMoney -= x.rentAmmount;
          x.rentTotalAmmount += x.rentAmmount;
        }

        //pay loan
        _monthMoney -= x.buyMonthlyPay;
        let _monthInterest =  calcularInteresCuota(x.buyTEA, _buyPendingToPay, 30);
        let _monthCapital = x.buyMonthlyPay - _monthInterest;
        _buyPendingToPay -= _monthCapital;
        
        x.buyFinalInterests += _monthInterest;
        x.buyFinalCost += _monthCapital + _monthInterest;


        //deposit
        x.depositTotalSaving += _monthMoney;
        x.depositTotalSaving += (x.depositTotalSaving * (x.depositTEA / 12));

        ///
        x.monthsToPay = i;

        if (x.depositTotalSaving > _buyPendingToPay) {
          x.depositMaxSaving = x.depositTotalSaving;
          x.depositTotalSaving -= _buyPendingToPay;
          x.buyFinalCost += _buyPendingToPay;
          break;
        }

        currentMonth = (currentMonth == 11) ? 0 : (currentMonth + 1);

      }
    }