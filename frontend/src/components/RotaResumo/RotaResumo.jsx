import React from 'react';

export default function RotaResumo({ data }) {
    if (!data || !data.caminhoMinimo || !data.caminhoMinimo.caminhoEncontrado) {
        return <p className="p-4 font-mono">Nenhum caminho mínimo encontrado.</p>;
    }

    const { caminhoMinimo } = data;
    const caminhoLetras = caminhoMinimo.caminho.map(index => String.fromCharCode(65 + index));

    return (
        <div className="p-4 md:p-8 w-full max-w-lg mx-auto border rounded-lg shadow-md mt-6">
            <h2 className="text-xl font-bold mb-4 font-mono">Resumo do Caminho Mínimo</h2>
            
            <table className="w-full border-collapse mb-4 text-center font-mono">
                <thead>
                    <tr>
                        <th className="border px-2 py-1">Ordem</th>
                        <th className="border px-2 py-1">Ponto</th>
                    </tr>
                </thead>
                <tbody>
                    {caminhoLetras.map((ponto, idx) => (
                        <tr key={idx}>
                            <td className="border px-2 py-1">{idx + 1}</td>
                            <td className="border px-2 py-1">{ponto}</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <p className="font-bold font-mono">Distância total: {caminhoMinimo.custo}</p>
        </div>
    );
}
