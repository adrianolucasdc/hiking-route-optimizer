import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import JourneyReport from '../Relatorio/JourneyReport'; // 1. Importe o novo componente de relatório

const getPointName = (index) => String.fromCharCode(65 + index);

export default function StopsMatrix({ count, onBack }) {
    const [matrix, setMatrix] = useState([]);
    const [currentRowIndex, setCurrentRowIndex] = useState(0);
    const [isFinished, setIsFinished] = useState(false); // 2. Estado para controlar a finalização

    useEffect(() => {
        const initialMatrix = Array.from({ length: count }, () =>
            Array.from({ length: count }, () => ({ checked: false, value: '' }))
        );
        setMatrix(initialMatrix);
        setCurrentRowIndex(0);
        setIsFinished(false); // Reseta o estado de finalização ao mudar a contagem
    }, [count]);

    // ... (as funções handleValueChange e handleCheckboxChange permanecem as mesmas)
    const handleValueChange = (colIndex, value) => {
        const newMatrix = [...matrix];
        newMatrix[currentRowIndex][colIndex].value = value;
        setMatrix(newMatrix);
    };

    const handleCheckboxChange = (colIndex) => {
        const newMatrix = [...matrix];
        const cell = newMatrix[currentRowIndex][colIndex];
        const isChecked = !cell.checked;
        
        newMatrix[currentRowIndex][colIndex] = {
            ...cell,
            checked: isChecked,
            value: isChecked ? cell.value : '',
        };
        setMatrix(newMatrix);
    };


    const handleNext = () => {
        const currentRow = matrix[currentRowIndex];
        const areCheckedCellsFilled = currentRow.every(cell => !cell.checked || (cell.checked && cell.value !== '' && !isNaN(cell.value)));

        if (!areCheckedCellsFilled) {
            toast.error("Por favor, preencha o valor para as conexões marcadas.");
            return;
        }

        if (currentRowIndex < count - 1) {
            setCurrentRowIndex(currentRowIndex + 1);
            toast.info(`Configurando Ponto ${getPointName(currentRowIndex + 1)}`);
        }
    };

    const handlePrevious = () => {
        if (currentRowIndex > 0) {
            setCurrentRowIndex(currentRowIndex - 1);
        }
    };

    const handleMatrixSubmit = () => {
        const lastRow = matrix[currentRowIndex];
        const areCheckedCellsFilled = lastRow.every(cell => !cell.checked || (cell.checked && cell.value !== '' && !isNaN(cell.value)));

        if (!areCheckedCellsFilled) {
            toast.error("Por favor, preencha o valor para as conexões marcadas.");
            return;
        }
        
        console.log("Dados Finais da Matriz:", matrix);
        toast.success("Jornada configurada com sucesso!");
        setIsFinished(true); // 3. Ativa o modo de relatório
    };

    // 4. Renderiza o relatório se a jornada estiver finalizada
    if (isFinished) {
        return <JourneyReport matrix={matrix} onBack={onBack} />;
    }

    if (matrix.length === 0) return null;

    const currentRow = matrix[currentRowIndex];

    return (
        <div className="p-4 md:p-8 w-full max-w-4xl mx-auto">
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold font-mono">
                    Ponto {getPointName(currentRowIndex)}: Defina as conexões
                </h2>
                <button
                    onClick={onBack}
                    className="bg-gray-500 text-white font-mono py-2 px-4 rounded-lg hover:bg-gray-600 text-sm"
                >
                    Alterar Quantidade
                </button>
            </div>

            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
                {Array.from({ length: count }).map((_, colIndex) => {
                    if (currentRowIndex === colIndex) {
                        return (
                            <div key={colIndex} className="p-4 border rounded-lg bg-gray-200 flex flex-col items-center justify-center min-h-[100px]">
                                <span className="font-bold">Ponto {getPointName(colIndex)}</span>
                                <span className="text-sm text-red-600 font-bold mt-2">Bloqueado</span>
                            </div>
                        );
                    }
                    
                    const isReverseChecked = matrix[colIndex]?.[currentRowIndex]?.checked;
                    if (isReverseChecked) {
                        return (
                            <div key={colIndex} className="p-4 border rounded-lg bg-gray-200 flex flex-col items-center justify-center min-h-[100px]">
                                <span className="font-bold">Ponto {getPointName(colIndex)}</span>
                                <span className="text-sm text-red-600 font-bold mt-2">Bloqueado</span>
                            </div>
                        );
                    }

                    const cellData = currentRow[colIndex];
                    return (
                        <div key={colIndex} className="p-4 border-2 border-black rounded-lg flex flex-col items-center justify-center gap-2 min-h-[100px]">
                            <label className="font-bold font-mono">Ponto {getPointName(colIndex)}</label>
                            <div className="flex items-center gap-2">
                                <input
                                    type="checkbox"
                                    className="h-5 w-5"
                                    checked={cellData.checked}
                                    onChange={() => handleCheckboxChange(colIndex)}
                                />
                                <input
                                    type="number"
                                    className="w-20 h-8 text-center border border-gray-400 rounded disabled:bg-gray-100 disabled:cursor-not-allowed"
                                    value={cellData.value}
                                    onChange={(e) => handleValueChange(colIndex, e.target.value)}
                                    disabled={!cellData.checked}
                                    placeholder="valor"
                                />
                            </div>
                        </div>
                    );
                })}
            </div>

            <div className="flex justify-between mt-8">
                <button
                    onClick={handlePrevious}
                    disabled={currentRowIndex === 0}
                    className="bg-black text-white font-bold font-mono py-2 px-8 rounded-lg hover:bg-gray-800 disabled:bg-gray-400 disabled:cursor-not-allowed"
                >
                    Anterior
                </button>

                {currentRowIndex < count - 1 ? (
                    <button
                        onClick={handleNext}
                        className="bg-black text-white font-bold font-mono py-2 px-8 rounded-lg hover:bg-gray-800"
                    >
                        Avançar
                    </button>
                ) : (
                    <button
                        onClick={handleMatrixSubmit}
                        className="bg-green-600 text-white font-bold font-mono py-2 px-8 rounded-lg hover:bg-green-700"
                    >
                        Finalizar Jornada
                    </button>
                )}
            </div>
        </div>
    );
}
