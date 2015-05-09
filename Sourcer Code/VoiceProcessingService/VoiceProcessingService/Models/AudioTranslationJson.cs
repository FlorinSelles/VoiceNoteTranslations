using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace VoiceProcessingService.Models
{
    class AudioTranslationJson: audioTranslation
    {
        public AudioNoteJson audioId;
        public language languageId;
    }
}
