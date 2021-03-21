# media_social_share

O melhor e mais completo plugin de compartilhamento nas mídias sociais e outros de forma nativa.

# Como usar:

## Compartilhar no feed do Instagram:
await MediaSocialShare.sharePostOnInstagram('caminhoImagem', 'legenda');

## Compartilhar no story do Instagram:
await MediaSocialShare.shareStoryOnInstagram('caminhoImagem');

## Compartilhar no feed do Facebook:
await MediaSocialShare.shareOnFacebook('caminhoImagem', 'legenda', 'facebookId');

## Compartilhar no story do Facebook:
await MediaSocialShare.shareStoryOnFacebook('caminhoImagem');

## Compartilhar no WhatsApp (status):
await MediaSocialShare.shareOnWhatsApp('caminhoImagem', 'legenda');

## Compartilhar no WhatsApp Business:
await MediaSocialShare.shareOnWhatsAppBusiness('caminhoImagem', 'legenda');

## Compartilhar em qualquer app possível de compartilhamento:
await MediaSocialShare.shareOnNative('caminhoImagem', 'legenda');