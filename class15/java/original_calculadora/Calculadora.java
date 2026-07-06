/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tdd.tddmock.calculadora;

/*
Desenvolva uma calculadora simples que seja capaz de realizar operações básicas 
de adição, subtração, multiplicação e divisão. A calculadora deve aceitar dois 
números como entrada e retornar o resultado da operação solicitada. Além disso, 
a calculadora deve tratar casos de erro, como divisão por zero.
*/

/**
 *
 * @author giovanebarcelos
 */
class Calculadora {

    int somar(int num1, int num2) {
        return num1 + num2;
    }

    int subtrair(int num1, int num2) {
        return num1 - num2;
    }

    int multiplicar(int num1, int num2) {
        return num1 * num2;
    }

    int dividir(int num1, int num2) throws DivisionByZeroException {
        if (num2 == 0){
            throw new DivisionByZeroException();
        }
        return num1 / num2;
    }
}
