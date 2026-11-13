export default function Footer() {
    return (
        <footer className="bg-white/95 dark:bg-gray-900/95 backdrop-blur-sm border-t border-gray-200 dark:border-gray-700 mt-auto">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                <div className="flex flex-col md:flex-row items-center justify-between gap-4">
                    <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
                        <svg className="w-4 h-4 text-green-600 dark:text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" />
                        </svg>
                        <span>© 2025 Hiking Route Optimizer. Projeto acadêmico open source.</span>
                    </div>
                    <div className="flex items-center gap-6 text-sm">
                        <a
                            href="https://github.com/adrianolucasdc/hiking-route-optimizer"
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-gray-600 dark:text-gray-400 hover:text-green-600 dark:hover:text-green-400 transition-colors"
                        >
                            GitHub
                        </a>
                        <a
                            href="#api"
                            className="text-gray-600 dark:text-gray-400 hover:text-green-600 dark:hover:text-green-400 transition-colors"
                        >
                            API Docs
                        </a>
                        <span className="px-2 py-0.5 text-xs font-medium bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200 rounded-full">
                            MIT License
                        </span>
                    </div>
                </div>
                <div className="mt-4 text-center text-xs text-gray-500 dark:text-gray-500">
                    Spring Boot 3.2.5 • React 19 • Python 3.11 • Docker • 122 testes
                </div>
            </div>
        </footer>
    )
}