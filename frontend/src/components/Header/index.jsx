import { useState, useRef, useEffect } from 'react';
import { useI18n } from '../../lib/I18nContext';

export default function Header({ currentTheme, onThemeChange }) {
  const { language, setLanguage, supportedLanguages, t } = useI18n();
  const [isLangOpen, setIsLangOpen] = useState(false);
  const langButtonRef = useRef(null);
  const langMenuRef = useRef(null);

  // Close language menu when clicking outside
  useEffect(() => {
    function handleClickOutside(event) {
      if (langButtonRef.current && !langButtonRef.current.contains(event.target) &&
          langMenuRef.current && !langMenuRef.current.contains(event.target)) {
        setIsLangOpen(false);
      }
    }

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <header className="fixed top-0 left-0 right-0 z-40 bg-bg/95 backdrop-blur-sm border-b border-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-14">
          <div className="flex items-center gap-2">
            <span className="text-lg font-semibold text-text-primary tracking-tight">{t('appTitle')}</span>
          </div>

          <div className="flex items-center gap-2">
            {/* Language Selector */}
            <div className="relative" ref={langButtonRef}>
              <button
                onClick={() => setIsLangOpen(!isLangOpen)}
                className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-surface-elevated border border-border hover:border-border-strong hover:bg-surface transition-all duration-150 text-sm font-medium text-text-secondary"
                aria-label={t('languageSelector')}
                aria-expanded={isLangOpen}
                aria-haspopup="listbox"
              >
                <span className="font-mono text-xs uppercase">{language.toUpperCase()}</span>
                <svg className={`w-3 h-3 transition-transform ${isLangOpen ? 'rotate-180' : ''}`} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                </svg>
              </button>

              {isLangOpen && (
                <ul
                  ref={langMenuRef}
                  role="listbox"
                  aria-label={t('languageSelector')}
                  className="absolute right-0 top-full mt-1.5 min-w-[140px] bg-surface border border-border rounded-lg shadow-lg py-1 z-50 animate-fade-in"
                >
                  {supportedLanguages.map((lang) => (
                    <li key={lang.code} role="option" aria-selected={language === lang.code}>
                      <button
                        onClick={() => {
                          setLanguage(lang.code);
                          setIsLangOpen(false);
                        }}
                        className={`w-full px-3 py-2 text-left text-sm transition-colors ${
                          language === lang.code
                            ? 'bg-accent/10 text-accent font-medium'
                            : 'text-text-secondary hover:bg-surface-elevated hover:text-text-primary'
                        }`}
                      >
                        <span className="flex items-center gap-2">
                          <span className="font-mono text-xs uppercase">{lang.name}</span>
                          <span className="text-text-muted">{lang.nativeName}</span>
                          {language === lang.code && (
                            <svg className="ml-auto w-4 h-4 text-accent" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                            </svg>
                          )}
                        </span>
                      </button>
                    </li>
                  ))}
                </ul>
              )}
            </div>

            {/* Theme Toggle */}
            <button
              onClick={onThemeChange}
              className="p-2 rounded-lg bg-surface-elevated border border-border hover:border-border-strong hover:bg-surface transition-all duration-150"
              aria-label={currentTheme === 'dark' ? t('themeToggleLight') : t('themeToggleDark')}
            >
              {currentTheme === 'dark' ? (
                <svg className="w-5 h-5 text-accent" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              ) : (
                <svg className="w-5 h-5 text-text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
                </svg>
              )}
            </button>

            {/* GitHub Link */}
            <a
              href="https://github.com/adrianolucasdc/hiking-route-optimizer"
              target="_blank"
              rel="noopener noreferrer"
              className="p-2 rounded-lg bg-surface-elevated border border-border hover:border-accent/50 hover:bg-surface transition-all duration-150"
              aria-label={t('github')}
            >
              <svg className="w-5 h-5 text-text-secondary hover:text-accent transition-colors" fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path d="M12 0C5.374 0 0 5.373 0 12c0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.305-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23A11.509 11.509 0 0112 5.803c1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576C20.566 21.797 24 17.3 24 12c0-6.627-5.373-12-12-12z"/>
              </svg>
            </a>
          </div>
        </div>
      </div>
    </header>
  );
}