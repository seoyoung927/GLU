import { Html, Head, Main, NextScript } from 'next/document';

export default function Document() {
  return (
    <Html lang="en">
      <Head>
        {/* 메타 태그를 Head 안에 넣어야 합니다 */}
        <meta
          name="google-site-verification"
          content="V5ZYJCNdt_HzDPX05cwk1u-mp_gc1Oe1jmoD2mFXPko"
        />
        <link rel="icon" type="image/png" sizes="48x48" href="/favicon.png" />
        <link rel="icon" type="image/x-icon" href="/favicon.ico" />
      </Head>
      <body>
        <Main />
        <NextScript />
      </body>
    </Html>
  );
}
