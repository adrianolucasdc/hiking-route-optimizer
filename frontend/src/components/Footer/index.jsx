import { useI18n } from '../../lib/I18nContext';

export default function Footer() {
  const { t } = useI18n();

  return (
    <footer className="bg-bg border-t border-border mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-5">
        <div className="flex flex-col md:flex-row items-center justify-between gap-3">
          <div className="flex items-center gap-2 text-sm text-text-muted">
            <span className="font-mono">{t('footerCopyright')}</span>
          </div>
          <div className="flex items-center gap-5 text-sm">
            <a
              href="https://github.com/adrianolucasdc/hiking-route-optimizer"
              target="_blank"
              rel="noopener noreferrer"
              className="text-text-muted hover:text-accent transition-colors"
            >
              {t('footerGithub')}
            </a>
            <a
              href="#api"
              className="text-text-muted hover:text-accent transition-colors"
            >
              {t('footerApiDocs')}
            </a>
            <span className="badge badge-neutral">
              {t('footerLicense')}
            </span>
          </div>
        </div>
        <div className="mt-3 text-center text-xs text-text-muted">
          {t('footerStack')}
        </div>
      </div>
    </footer>
  )
}